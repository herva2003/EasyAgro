package com.puc.easyagro.ui.perfil.login_cadastro.cadastro.completarCadastro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.puc.easyagro.R
import com.puc.easyagro.apiServices.UserApi
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.databinding.FragmentCompleteCadastroBinding
import com.puc.easyagro.model.UserDTO
import com.puc.easyagro.services.StorageFirebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CompleteCadastroFragment : Fragment() {

    private lateinit var binding: FragmentCompleteCadastroBinding

    private val listUrl = ArrayList<String>(1)

    private lateinit var imageSlider: ImageSlider

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCompleteCadastroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usuario: UserDTO? = arguments?.getParcelable("usuario")

        imageSlider = binding.imageSlider

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.imageView.setOnClickListener{
            openGallery()
        }

        binding.btnRegister.setOnClickListener {
            if (validateInputFields() && usuario != null) {
                if (validateInputFields()) {
                    val apelido = binding.inputApelido.text.toString()
                    val nome = binding.inputNome.text.toString()
                    val telefone = binding.inputTelefone.text.toString()
                    val imagem = listUrl[0]

                    usuario.nickname =apelido
                    usuario.name = nome
                    usuario.phoneNumber = telefone
                    usuario.imagem = imagem

                    Log.d("12",usuario.toString())
                    val action = CompleteCadastroFragmentDirections.actionCompleteCadastroFragmentToAddressFragment2(usuario)
                    findNavController().navigate(action, navOptions)
                }
            }
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val storageFirebase = StorageFirebase("images/userImages")
            val imageUri = result.data?.data
            if (imageUri != null) {

                storageFirebase.uploadImage(imageUri, object :
                    StorageFirebase.OnImageUploadListener {
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
        val storageFirebase = StorageFirebase("images/userImages")
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(galleryIntent)
    }

    private fun validateInputFields(): Boolean {
        // Validation Rules
        val nomeValidation: Boolean = binding.inputNome.text.toString().trim().isNotEmpty()
        val telefoneValidation: Boolean = binding.inputTelefone.text.toString().trim().isNotEmpty()
        val apelidoValidation: Boolean = binding.inputApelido.text.toString().trim().isNotEmpty()
        val image: Boolean = listUrl.isNotEmpty()

        // Validation Message
        val blankMessage = "Esse campo não pode ser vazio"

        if (!nomeValidation  ) {
            binding.inputNome.error = blankMessage
        }


        if (!telefoneValidation) {
            binding.inputTelefone.error = blankMessage
        }

        if (!apelidoValidation) {
            binding.inputApelido.error = blankMessage
        }

        if (!image) {
            Toast.makeText(activity, "Por favor, selecione uma imagem!", Toast.LENGTH_SHORT).show()
        }

        if (!nomeValidation || !telefoneValidation || !apelidoValidation || !image) {
            Toast.makeText(activity, "Tente novamente!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}