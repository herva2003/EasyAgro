package com.puc.easyagro.ui.market.pagamento

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.puc.easyagro.R
import com.puc.easyagro.apiServices.PaymentAPi
import com.puc.easyagro.apiServices.UserApi
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.databinding.FragmentPagamentoBinding
import com.puc.easyagro.datastore.UserPreferencesRepository
import com.puc.easyagro.model.PayerDTO
import com.puc.easyagro.model.PayerIdentificationDTO
import com.puc.easyagro.model.PixPaymentDTO
import com.puc.easyagro.model.PixPaymentResponseDTO
import com.puc.easyagro.model.ProdutosPix
import com.puc.easyagro.ui.market.MarketAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal

class PagamentoFragment : Fragment() {

    private var _binding: FragmentPagamentoBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PagamentoAdapter

    private var qrCode: String = "";
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentPagamentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchDataFromServer()

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        adapter = PagamentoAdapter(emptyList()) { _, _ ->}

        binding.btnArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnComprar.setOnClickListener {

            val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())
            val carrinhoList = adapter.getData()

            if (carrinhoList.isNotEmpty()) {

                val payerDTO = PayerDTO(
                    firstName = "josé",
                    lastName = "silva",
                    email = "vivofixodante@gmail.com",
                    identification = PayerIdentificationDTO(
                        type = "cpf",
                        number = "30089374894"
                    )
                )

                val pixPaymentDTO = PixPaymentDTO(
                    transactionAmount = BigDecimal(2),
                    productDescription = "compra feito pelo app" ,
                    payer = payerDTO,
                    orders = carrinhoList.map { produto ->
                        ProdutosPix(
                            productId = produto.id,
                            quantity = produto.quantityInStock,
                            buyerId = userPreferencesRepository.userId,
                            sellerId = produto.userId,
                            price =  produto.price
                        )
                    }
                )
                makePayment(pixPaymentDTO)

            } else {
                Toast.makeText(context, "Carrinho está vazio!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun makePayment(pixPaymentDTO: PixPaymentDTO) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val paymentApi = retrofit.create(PaymentAPi::class.java)

        val call = paymentApi.buyProducts(pixPaymentDTO)

        call.enqueue(object : Callback<PixPaymentResponseDTO> {
            override fun onResponse(
                call: Call<PixPaymentResponseDTO>,
                response: Response<PixPaymentResponseDTO>
            ) {
                if (response.isSuccessful) {
                    val paymentResponse = response.body()
                    qrCode = paymentResponse!!.qrCode
                    showCopyCodeDialog(qrCode)
                    Log.d("Payment", "Payment successful. Response: $paymentResponse.")


                } else {
                    Log.e("Payment", "Payment failed. Code: ${response.code()}, Message: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PixPaymentResponseDTO>, t: Throwable) {
                Log.e("Payment", "Payment failed. Error: ${t.message}", t)
            }
        })
    }

    private fun showCopyCodeDialog(codeToCopy: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_custom_dialog, null)

        dialogView.findViewById<TextView>(R.id.textViewCode).text = codeToCopy

        dialogView.findViewById<Button>(R.id.btnCopy).setOnClickListener {
            val clipboardManager =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Código", codeToCopy)
            clipboardManager.setPrimaryClip(clipData)

            Snackbar.make(
                dialogView,
                "Código copiado para a área de transferência",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.show()
    }

    private fun fetchDataFromServer() {

        val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())
        val userId = userPreferencesRepository.userId

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(UserApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getItemCarrinho(userId).execute()
                if (response.isSuccessful) {
                    val carrinhoList = response.body() ?: emptyList()

                    val total = carrinhoList.sumByDouble { it.price?.toDouble() ?: 0.0 }

                    Log.d("car", "Produtos: $carrinhoList")

                    launch(Dispatchers.Main) {
                        binding.txtSubtotalPrice.text = "R$ $total"
                        binding.txtTaxaServicoPrice.text = "R$ 0,0"
                        binding.txtTotalPrice.text = "R$ $total"
                        adapter.updateData(carrinhoList)
                    }
                }
            } catch (e: Exception) {
                Log.e("car", "Exception during data fetch", e)
            }
        }
    }
}