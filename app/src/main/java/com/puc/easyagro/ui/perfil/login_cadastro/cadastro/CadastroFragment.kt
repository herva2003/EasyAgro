package com.puc.easyagro.ui.perfil.login_cadastro.cadastro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.puc.easyagro.R
import com.puc.easyagro.databinding.FragmentCadastroBinding
import com.puc.easyagro.datastore.UserViewModel
import com.puc.easyagro.model.UserDTO

class CadastroFragment : Fragment() {

    private var _binding: FragmentCadastroBinding? = null
    val viewModel: UserViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCadastroBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = binding.inputEmail.text.toString()
        val password = binding.inputSenha.text.toString()

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        binding.btnRegister.setOnClickListener {
            if (validateInputFields()) {
                val usuario = getFormData()
                val action = CadastroFragmentDirections.actionCadastroFragmentToCompleteCadastroFragment(usuario)
                findNavController().navigate(action, navOptions)
            }
        }

        binding.btnEntrar.setOnClickListener {
            val action = CadastroFragmentDirections.actionCadastroFragmentToLoginFragment()
            findNavController().navigate(action, navOptions)
        }
    }

    private fun getFormData(): UserDTO {
        val email = binding.inputEmail.text.toString().trim()
        val senha = binding.inputSenha.text.toString().trim()
        val cpf = binding.inputCpf.text.toString().trim()

        return UserDTO(login = email, password = senha, nickname = null, name = null,phoneNumber = null, cpf = cpf)
    }

    private fun validateInputFields(): Boolean {
        // Validation Rules
        val emailValidation: Boolean = binding.inputEmail.text.toString().trim().isNotEmpty()
        val passwordValidation: Boolean = binding.inputSenha.text.toString().isNotEmpty()
        val confirmPasswordValidation: Boolean =
            binding.inputSenhaConfirm.text.toString().isNotEmpty()
        val passwordSameValidation: Boolean =
            binding.inputSenha.text.toString() == binding.inputSenhaConfirm.text.toString()

        // Validation Message
        val blankMessage = "Esse campo não pode ser vazio"
        val passwordSameMessage = "As senhas devem ser iguais"

        if (!emailValidation) {
            binding.inputEmail.error = blankMessage
        }

        if (!passwordValidation) {
            binding.inputSenha.error = blankMessage
        }

        if (!confirmPasswordValidation) {
            binding.inputSenhaConfirm.error = blankMessage
        }

        if (!passwordSameValidation) {
            binding.inputSenha.error = passwordSameMessage
            binding.inputSenhaConfirm.error = passwordSameMessage
        }

        if (!emailValidation || !passwordValidation || !confirmPasswordValidation || !passwordSameValidation) {
            Toast.makeText(activity, "Tente novamente!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
