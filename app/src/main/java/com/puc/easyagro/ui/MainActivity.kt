package com.puc.easyagro.ui

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.puc.easyagro.R
import com.puc.easyagro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

        // Configuração da visibilidade da BottomNavigationView
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // Oculta a BottomNavigationView em destinos específicos
                R.id.tarefaFragment -> navView.visibility = View.GONE
                R.id.culturasFragment -> navView.visibility = View.GONE
                R.id.detalhesCulturaFragment -> navView.visibility = View.GONE
                R.id.detalhesFinalFragment -> navView.visibility = View.GONE
                R.id.detalhesItemFragment -> navView.visibility = View.GONE
                R.id.addProdutoFragment -> navView.visibility = View.GONE
                R.id.loginFragment -> navView.visibility = View.GONE
                R.id.cadastroFragment -> navView.visibility = View.GONE
                R.id.cotacaoFragment -> navView.visibility = View.GONE
                R.id.viewItemMarketFragment -> navView.visibility = View.GONE
                R.id.completeCadastroFragment -> navView.visibility = View.GONE
                R.id.minhaContaFragment -> navView.visibility = View.GONE
                R.id.meusAnunciosFragment -> navView.visibility = View.GONE
                R.id.minhasVendasFragment -> navView.visibility = View.GONE
                R.id.minhasComprasFragment -> navView.visibility = View.GONE
                R.id.favoritosFragment -> navView.visibility = View.GONE
                R.id.carrinhoFragment -> navView.visibility = View.GONE

                else -> navView.visibility = View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            // Se houver fragmentos na pilha de retrocesso, volte ao anterior
            supportFragmentManager.popBackStack()
        } else {
            // Caso contrário, execute o comportamento padrão (sair da atividade, por exemplo)
            super.onBackPressed()
        }
    }
}
