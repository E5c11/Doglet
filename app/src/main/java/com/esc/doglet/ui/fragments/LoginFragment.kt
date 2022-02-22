package com.esc.doglet.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.esc.doglet.R
import com.esc.doglet.databinding.LoginFragmentBinding
import com.esc.doglet.utils.LetterWatcher
import com.esc.doglet.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "myT"

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.login_fragment) {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding : LoginFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)
        setLoginListeners()
        setLoginObservers()
    }

    private fun setLoginObservers() {
        viewModel.getLoggedIn().observe(viewLifecycleOwner, {
            findNavController().navigate(LoginFragmentDirections.actionLoginToMapFragment())})
        viewModel.getEmailError().observe(viewLifecycleOwner, { binding.regEmail.error = it })
        viewModel.getPasswordError().observe(viewLifecycleOwner, { binding.regPassword.error = it })
    }

    private fun setRegisterObservers() {
        viewModel.getNameError().observe(viewLifecycleOwner, {binding.displayNameInput.error = it })
        viewModel.getPassConError().observe(viewLifecycleOwner, {binding.regPasswordInputConEdit.error = it})
    }

    private fun setProgressBar(vis: Int) {
        val an = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_fast)
        binding.registerProgress.animation = an
        binding.registerProgress.visibility = vis
    }

    private fun setLoginListeners() {
        binding.registerText.setOnClickListener { enableLogin() }
        binding.regEmailInput.addTextChangedListener(object : LetterWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().contains("@") && s.toString().contains("."))
                    viewModel.isValidEmail(s.toString())
            }
        })
        binding.regPasswordInput.setOnFocusChangeListener{ v, b ->
            if (!b) viewModel.setPassword(binding.regPassword.editText?.text.toString().trim { it <= ' ' })
            else changeConstraints()
        }
        binding.regEmailInput.setOnFocusChangeListener { v, b ->
            if (b) changeConstraints()
            else viewModel.isValidEmail(getEmailText())
        }
        binding.submit.setOnClickListener{
            if (binding.submit.text.equals(resources.getString(R.string.register))) {
                setProgressBar(View.VISIBLE)
                viewModel.setName(binding.regName.editText?.text.toString().trim { it <= ' ' })
                viewModel.setEmail(getEmailText())
                viewModel.setPassCon(binding.regPasswordCon.editText?.text.toString().trim { it <= ' ' })
                viewModel.submitNewUser()
            }
        }
    }

    private fun setRegisterListeners() {
        binding.regPasswordInputConEdit.setOnFocusChangeListener { v, b ->
            if (b) changeConstraints()
        }
    }

    private fun getEmailText(): String {
        return binding.regEmail.editText?.text.toString().trim() {it <= ' '}
    }

    private fun changeConstraints() {
        val params = binding.regScroll.layoutParams as ConstraintLayout.LayoutParams
        params.bottomToTop = binding.submit.id
        binding.regScroll.requestLayout()
    }

    private fun enableLogin() {
        binding.regName.visibility = View.VISIBLE
        binding.regPasswordCon.visibility = View.VISIBLE
        binding.submit.text = resources.getString(R.string.register)
        setRegisterObservers()
        setRegisterListeners()
    }

}
