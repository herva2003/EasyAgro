import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.teethkids.utils.Utils
import com.example.teethkids.utils.Utils.hideKeyboard
import com.puc.easyagro.R
import com.puc.easyagro.apiServices.UserApi
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.databinding.FragmentAddress2Binding
import com.puc.easyagro.model.AddressDto
import com.puc.easyagro.model.UserDTO
import com.puc.easyagro.model.UserDTO2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddressFragment : Fragment() {
    private var _binding: FragmentAddress2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddress2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usuario: UserDTO? = arguments?.getParcelable("usuario")
        binding.edtZipe.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val cep = binding.edtZipe.unMasked
                Log.d("122", cep)
                if (cep.isNotEmpty() && isValidCep(cep)) {
                    lifecycleScope.launch {
                        getAddressByCep(cep)
                    }
                } else {
                    binding.zipCodeInputLayout.endIconDrawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.baseline_close_24)
                }
            }
        })

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCadastrar.setOnClickListener{
            if (isValid() && usuario != null) {
                val zipe = binding.edtZipe.unMasked
                val street = binding.edtStreet.text.toString().trim()
                val number = binding.edtNumber.text.toString().trim()
                val neighborhood = binding.edtNeighbBorhood.text.toString().trim()
                val city = binding.edtCity.text.toString().trim()
                val state = binding.edtState.text.toString().trim()

                val addressDto = AddressDto(cep = zipe, logradouro = street, numero = number, bairro = neighborhood, localidade = city, uf = state)

                val userDTO = UserDTO2(
                    name = usuario.name,
                    nickname = usuario.nickname,
                    login = usuario.login,
                    password = usuario.password,
                    phoneNumber = usuario.phoneNumber,
                    cpf = usuario.cpf,
                    address = addressDto
                )


                Log.d("12", userDTO.toString())

                registerUser(userDTO)
            }
        }



    }

    private fun registerUser(conta: UserDTO2) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(UserApi::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d("cad", "Criando conta: ${conta?.login}")
                val response = apiService.addUser(conta).execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            context,
                            "Conta criada com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.action_addressFragment2_to_loginFragment)
                    } else {
                        Log.d("cad", "Falha ao criar conta: ${response.errorBody()?.string()}")
                        Toast.makeText(
                            context,
                            "Falha ao criar conta. Tente novamente.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("cad", "Erro ao criar conta: ${e.message}")
            }
        }
    }

    private suspend fun getAddressByCep(cep: String) {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(UserApi::class.java)

            try {
                val response = apiService.getAddressByCep(cep)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        hideKeyboard()
                        binding.zipCodeInputLayout.endIconDrawable =
                            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_done_24)

                        binding.edtStreet.setText(responseBody?.logradouro)
                        binding.edtNeighbBorhood.setText(responseBody?.bairro)
                        binding.edtCity.setText(responseBody?.localidade)
                        binding.edtState.setText(responseBody?.uf)
                    } else {
                        Utils.showToast(
                            requireContext(),
                            "Não foi possível carregar os dados do CEP. Por favor, digite manualmente."
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AddAddressDialog", "Erro na resposta: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e("AddAddressDialog", "Erro ao criar Retrofit: ${e.message}")
        }
    }

    private fun isValidCep(zipeCode: String): Boolean {
        return zipeCode.length >= 8
    }

    fun isValid(): Boolean {
        val zipe = binding.edtZipe.unMasked
        val street = binding.edtStreet.text.toString().trim()
        val number = binding.edtNumber.text.toString().trim()
        val neighborhood = binding.edtNumber.text.toString().trim()
        val city = binding.edtCity.text.toString().trim()
        val state = binding.edtState.text.toString().trim()

        if (zipe.isEmpty()) {
            binding.edtZipe.error = "Cep não pode ser vazio"
            return false
        }

        if (street.isEmpty()) {
            binding.edtStreet.error = "Rua não pode ser vazio"
            return false
        }

        if (number.isEmpty()) {
            binding.edtNumber.error = "Numero não pode ser vazio"
            return false
        }
        if (neighborhood.isEmpty()) {
            binding.edtNeighbBorhood.error = "Bairro não pode ser vazio"
            return false
        }

        if (city.isEmpty()) {
            binding.edtCity.error = "Cidade não pode ser vazio"
            return false
        }
        if (state.isEmpty()) {
            binding.edtState.error = "Estado não pode ser vazio"
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
