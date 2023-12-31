package com.puc.easyagro.ui.market.addProduto

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.gson.Gson
import com.puc.easyagro.R
import com.puc.easyagro.apiServices.MarketApi
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.databinding.DialogClearAdBinding
import com.puc.easyagro.databinding.FragmentAddProdutoBinding
import com.puc.easyagro.datastore.UserPreferencesRepository
import com.puc.easyagro.model.MarketDTO
import com.puc.easyagro.services.StorageFirebase
import com.puc.easyagro.services.StorageFirebase.OnImageUploadListener
import com.puc.easyagro.ui.market.MarketFragmentDirections
import com.puc.easyagro.ui.market.getCategoriasAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AddProdutoFragment : Fragment() {

    private var _binding: FragmentAddProdutoBinding? = null

    private val binding get() = _binding!!

    private val listUrl = ArrayList<String>(5)

    private lateinit var imageSlider: ImageSlider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProdutoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageSlider = binding.imageSlider

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        binding.toolbar.btnArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAnunciar.setOnClickListener {
            if (validateInputFields()) {
                sendDataToServer(getFormData())
                val action = AddProdutoFragmentDirections.actionAddProdutoFragmentToMarketFragment()
                findNavController().navigate(action, navOptions)
            }
        }

        binding.imageView.setOnClickListener{
            openGallery()
        }

        val adapter = getCategoriasAdapter(requireContext())
        binding.categoriasSpinner.adapter = adapter

        binding.toolbar.screenName.text = "Inserir Anúncio"

        binding.toolbar.btnClear.setOnClickListener { showCustomDialog() }

        val customTextView = binding.infoText
        val textoCompleto = customTextView.text.toString()
        val spannableString = SpannableString(textoCompleto)
        val corDestaque = "#018241"

        val indicesCampos = textoCompleto.indexOf("campos")
        val indicesAsterisco = textoCompleto.indexOf("(*)")
        val indicesObrigatorios = textoCompleto.indexOf("obrigatórios")

        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor(corDestaque)),
            indicesCampos,
            indicesCampos + "campos".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor(corDestaque)),
            indicesAsterisco,
            indicesAsterisco + "(*)".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor(corDestaque)),
            indicesObrigatorios,
            indicesObrigatorios + "obrigatórios".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        customTextView.text = spannableString
    }

    private fun showCustomDialog(){
        val customDialog = DialogClearAdBinding.inflate(LayoutInflater.from(requireContext()))
        val builder = AlertDialog.Builder(requireContext())

        builder.setView(customDialog.root)

        val dialog = builder.create()

        customDialog.btnAccept.setOnClickListener {
            clearAd()
            dialog.dismiss()
        }

        customDialog.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun clearAd() {
        binding.anuncioInput.setText("")
        binding.descricaoInput.setText("")
        binding.precoInput.setText("")
        binding.anuncioInput.requestFocus()

        listUrl.clear()
        binding.imageSlider.setImageList(emptyList())
    }

    private fun getFormData(): MarketDTO {
        val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())
        val userId = userPreferencesRepository.userId
        Log.d("22", userId)
        val name = binding.anuncioInput.text.toString()
        val category = binding.categoriasSpinner.selectedItem.toString()
        val price = binding.precoInput.text.toString().toBigDecimal()
        val description = binding.descricaoInput.text.toString()


        return MarketDTO(name = name, price = price, category = category,
            description = description, userId = userId, images = listUrl)
    }

    private fun validateInputFields(): Boolean {
        // Validation Rules
        val name: Boolean = binding.anuncioInput.text.toString().trim().isNotEmpty()
        val price: Boolean = binding.precoInput.text.toString().trim().isNotEmpty()
        val category: Boolean =
            binding.categoriasSpinner.selectedItem.toString().trim().isNotEmpty()
        val description: Boolean = binding.descricaoInput.text.toString().trim().isNotEmpty()
        val image: Boolean = listUrl.isNotEmpty()

        val blankMessage = "Esse campo não pode ser vazio"

        if (!name) {
            binding.anuncioInput.error = blankMessage
        }

        if (!price) {
            binding.precoInput.error = blankMessage
        }

        if (!category) {
            val errorView = binding.categoriasSpinner.selectedView as? TextView
            errorView?.error = blankMessage
        }

        if (!description) {
            binding.descricaoInput.error = blankMessage
        }

        if (!image) {
            Toast.makeText(activity, "Por favor, selecione uma imagem!", Toast.LENGTH_SHORT).show()
        }

        if (!name || !price || !category || !description || !image) {
            Toast.makeText(activity, "Tente novamente!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }


    private fun sendDataToServer(produto: MarketDTO) {

        val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())

        val interceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Authorization", "Bearer ${userPreferencesRepository.token}")
                .build()
            chain.proceed(request)
        }
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        val apiService = retrofit.create(MarketApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d("mkt", "Enviando produto: $produto")
                val response = apiService.addProduct(produto).execute()
                if (response.isSuccessful) {
                    val gson = Gson()
                    val produtoInserido: MarketDTO = gson.fromJson(response.body()?.string(), MarketDTO::class.java)
                    Log.d("mkt", "Produto inserido com sucesso: $produtoInserido")

                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "Produto adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Log.d("mkt", "Falha ao inserir produto: $response")
                }
            } catch (e: Exception) {
                Log.d("mkt", "Excessão ao inserir produto: $e")
            }
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val storageFirebase = StorageFirebase("images/produtosMercado")
            val imageUri = result.data?.data
            if (imageUri != null) {

                storageFirebase.uploadImage(imageUri, object : OnImageUploadListener {
                    override fun onSuccess(imageUrl: String) {
                        listUrl.add(imageUrl)
                        Log.d("photo", "Lista de URLs após upload: $listUrl")

                        // Atualizar o imageSlider
                        val imageList = mutableListOf<SlideModel>()
                        for (url in listUrl) {
                            imageList.add(SlideModel(url))
                        }
                        imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)
                    }

                    override fun onFailure(errorMessage: String) {
                        // O upload falhou, trate o erro aqui
                        Log.e("mkt", "Erro no upload da imagem: $errorMessage")
                    }
                })

            }
        }
    }

    private fun openGallery() {
        val storageFirebase = StorageFirebase("images/produtosMercado")
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(galleryIntent)
    }


}
