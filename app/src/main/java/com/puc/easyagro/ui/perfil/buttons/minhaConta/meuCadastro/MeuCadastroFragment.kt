package com.puc.easyagro.ui.perfil.buttons.minhaConta.meuCadastro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.puc.easyagro.apiServices.UserApi
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.databinding.FragmentMeuCadastroBinding
import com.puc.easyagro.datastore.UserPreferencesRepository
import com.puc.easyagro.model.AddressDto
import com.puc.easyagro.model.UserUpdateDTO
import com.puc.easyagro.model.Usuario
import com.puc.easyagro.services.StorageFirebase
import com.puc.easyagro.ui.dialogs.AddressBottomSheetFragment
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MeuCadastroFragment : Fragment() {
    private lateinit var binding: FragmentMeuCadastroBinding

    private var myAddress: AddressDto? = null
    private lateinit var imageView: ImageView

    private val listUrl = ArrayList<String>(1)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMeuCadastroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = binding.imgPfp

        binding.arrowImage.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnUpdate.setOnClickListener{
            updateUserOnServer()
        }

        binding.imageView.setOnClickListener{
            openGallery()
        }

        binding.btnVerEndereco.setOnClickListener {
            myAddress?.let {
                val bottomSheetFragment = AddressBottomSheetFragment(it)
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }
        }

        fetchUserFromServer()
    }

    private fun fetchUserFromServer() {

        val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())
        val userId = userPreferencesRepository.userId

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(UserApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getUser(userId).execute()
                if (response.isSuccessful) {
                    val user = response.body()
                    Log.d("mcf","User Address: $user")
                    launch(Dispatchers.Main) {
                        binding.inputApelido.setText(user?.nickname)
                        binding.inputTelefone.setText(user?.phoneNumber)
                        binding.inputNome.setText(user?.name)
                        myAddress = user?.address

                        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                        Picasso.get().load(user?.imagem).into(imageView)
                    }
                }
            } catch (e: Exception) {
                Log.e("mcf", "Exception during data fetch", e)
            }
        }
    }


    private fun updateUserOnServer() {

        val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())
        val userId = userPreferencesRepository.userId

        Log.d("mcf", "UserId: $userId")
        Log.d("mcf", "listUrl: $listUrl")

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(UserApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val user = UserUpdateDTO(
                    nickname = binding.inputApelido.text.toString(),
                    name = binding.inputNome.text.toString(),
                    cpf = 223,
                    phoneNumber = binding.inputTelefone.text.toString(),
                    imagem = listUrl[0]
                )

                val response = apiService.updateUser(userId, user).execute()
                if (response.isSuccessful) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "Usuário atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                        Log.d("mcf", "User atualizado: $user")
                    }
                }
            } catch (e: Exception) {
                Log.e("mcf", "Exception during data update", e)
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

                        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                        Picasso.get().load(imageUrl).into(imageView)

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
}