package com.puc.easyagro.ui.perfil.buttons

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.puc.easyagro.R
import com.puc.easyagro.apiServices.CarrinhoApi
import com.puc.easyagro.apiServices.MarketApi
import com.puc.easyagro.apiServices.PaymentAPi
import com.puc.easyagro.apiServices.UserApi
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.databinding.FragmentCarrinhoBinding
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

class CarrinhoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MarketAdapter

    private var _binding: FragmentCarrinhoBinding? = null


    private var qrCode: String = "";

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentCarrinhoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerViewMarket

        val numberOfColumns = 1
        recyclerView.layoutManager = GridLayoutManager(requireContext(), numberOfColumns)

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        adapter = MarketAdapter(emptyList()) { itemId, _ ->
            val action =
                CarrinhoFragmentDirections.actionCarrinhoFragmentToViewItemMarketFragment(itemId)
            findNavController().navigate(action, navOptions)
        }

        binding.btnArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        recyclerView.adapter = adapter

        fetchDataFromServer()

        val pullToRefresh = binding.pullToRefresh
        pullToRefresh.setOnRefreshListener {
            fetchDataFromServer()
            pullToRefresh.isRefreshing = false
        }

        val buscarProduto = binding.buscarProduto

        buscarProduto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                adapter.filter.filter(s.toString())
            }
        })

        // isso aqui
        binding.btnComprar.setOnClickListener {

            val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())
            val carrinhoList = adapter.getData()

            if (carrinhoList.isNotEmpty()) {


                // user info....
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

            }
        }

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

                    Log.d("car", "Produtos: $carrinhoList")

                    launch(Dispatchers.Main) {
                        adapter.updateData(carrinhoList)
                    }
                }
            } catch (e: Exception) {
                Log.e("car", "Exception during data fetch", e)
            }
        }


    }



    // isso
    private fun makePayment(pixPaymentDTO: PixPaymentDTO) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL) // Substitua pela URL correta da sua API de pagamento
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



}