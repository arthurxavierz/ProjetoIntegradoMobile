package com.example.gestaoacademica2

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gestaoacademica2.databinding.ActivityMainBinding
import com.example.gestaoacademica2.ui.MainViewModel
import com.example.gestaoacademica2.ui.addedit.AddEditBottomSheet
import com.example.gestaoacademica2.ui.tabs.AgendaFragment
import com.example.gestaoacademica2.ui.tabs.FavoritosFragment
import com.example.gestaoacademica2.ui.tabs.HojeFragment
import com.example.gestaoacademica2.ui.tabs.PerfilFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModel.Factory((application as AcademicApp).repository, this)
        )[MainViewModel::class.java]
    }

    private enum class Tab { HOJE, AGENDA, FAV, PERFIL }

    private val fragments: Map<Tab, Fragment> by lazy {
        mapOf(
            Tab.HOJE to HojeFragment(),
            Tab.AGENDA to AgendaFragment(),
            Tab.FAV to FavoritosFragment(),
            Tab.PERFIL to PerfilFragment(),
        )
    }
    private var currentTab = Tab.HOJE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        if (savedInstanceState == null) setupFragments()
        setupNav()
        setupFab()
        setupLogin()
        observeSession()
        updateNav(Tab.HOJE)
    }

    /** API 36 força edge-to-edge: aplicamos os insets manualmente para reproduzir o
     *  protótipo (faixa verde na status bar, nav acima da barra de gestos). */
    private fun setupEdgeToEdge() {
        // Ícones da status bar claros (a faixa é verde escura)
        WindowCompat.getInsetsController(window, binding.root).isAppearanceLightStatusBars = false

        val fabBaseMargin = (88 * resources.displayMetrics.density).toInt()
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout) { _, insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

            binding.statusBarBand.updateLayoutParams { height = top }
            binding.contentColumn.updatePadding(top = top)
            binding.bottomNavBar.updatePadding(bottom = bottom)
            binding.loginContainer.updatePadding(top = top)
            binding.fab.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = fabBaseMargin + bottom
            }
            insets
        }
    }

    // ── Fragments ──
    private fun setupFragments() {
        supportFragmentManager.commit {
            fragments.forEach { (tab, frag) ->
                add(R.id.contentHost, frag, tab.name)
                if (tab != Tab.HOJE) hide(frag)
            }
        }
    }

    private fun switchTab(tab: Tab) {
        if (tab == currentTab) return
        supportFragmentManager.commit {
            fragments[currentTab]?.let { hide(it) }
            fragments[tab]?.let { show(it) }
        }
        currentTab = tab
        updateNav(tab)
    }

    // ── Bottom nav ──
    private fun setupNav() {
        binding.navHoje.setOnClickListener { switchTab(Tab.HOJE) }
        binding.navAgenda.setOnClickListener { switchTab(Tab.AGENDA) }
        binding.navFav.setOnClickListener { switchTab(Tab.FAV) }
        binding.navPerfil.setOnClickListener { switchTab(Tab.PERFIL) }
    }

    private fun updateNav(tab: Tab) {
        val active = ContextCompat.getColor(this, R.color.brand_emerald)
        val inactive = ContextCompat.getColor(this, R.color.text_muted)

        data class Item(val icon: android.widget.ImageView, val text: TextView, val tab: Tab)
        val items = listOf(
            Item(binding.navHojeIcon, binding.navHojeText, Tab.HOJE),
            Item(binding.navAgendaIcon, binding.navAgendaText, Tab.AGENDA),
            Item(binding.navFavIcon, binding.navFavText, Tab.FAV),
            Item(binding.navPerfilIcon, binding.navPerfilText, Tab.PERFIL),
        )
        items.forEach { item ->
            val on = item.tab == tab
            val tint = if (on) active else inactive
            item.icon.setColorFilter(tint)
            item.text.setTextColor(tint)
            item.text.typeface = dmSans(if (on) 700 else 500)
        }

        // FAB só aparece em Hoje e Agenda
        binding.fab.visibility =
            if (tab == Tab.HOJE || tab == Tab.AGENDA) View.VISIBLE else View.GONE
    }

    // ── FAB ──
    private fun setupFab() {
        binding.fab.setOnClickListener {
            AddEditBottomSheet.newInstance(null).show(supportFragmentManager, "addEdit")
        }
    }

    // ── Login / Cadastro ──
    private var isRegister = false

    private fun setupLogin() {
        val v = binding.loginView
        v.btnLogin.setOnClickListener { submitAuth() }
        v.btnToggleMode.setOnClickListener { toggleMode() }
        listOf(v.etName, v.etEmail, v.etPassword, v.etConfirm).forEach {
            it.doAfterTextChanged { hideError() }
        }
        applyMode()
    }

    private fun toggleMode() {
        isRegister = !isRegister
        binding.loginView.etConfirm.text?.clear()
        hideError()
        applyMode()
    }

    /** Ajusta os campos visíveis e os rótulos conforme o modo (login x cadastro). */
    private fun applyMode() {
        val v = binding.loginView
        v.layoutName.visibility = if (isRegister) View.VISIBLE else View.GONE
        v.layoutConfirm.visibility = if (isRegister) View.VISIBLE else View.GONE
        v.tvForgot.visibility = if (isRegister) View.GONE else View.VISIBLE
        v.tvLoginSubtitle.setText(
            if (isRegister) R.string.login_subtitle_register else R.string.login_subtitle
        )
        v.btnLogin.setText(if (isRegister) R.string.login_create else R.string.login_enter)
        v.btnToggleMode.setText(if (isRegister) R.string.login_have_account else R.string.login_create)
    }

    private fun submitAuth() {
        hideKeyboard()
        val v = binding.loginView
        val name = v.etName.text.toString().trim()
        val email = v.etEmail.text.toString().trim()
        val pass = v.etPassword.text.toString()
        val confirm = v.etConfirm.text.toString()

        val error: String? = when {
            isRegister && name.isBlank() -> "Informe seu nome."
            !email.contains("@") -> "Informe um e-mail válido."
            pass.length < 6 -> "Senha deve ter ao menos 6 caracteres."
            isRegister && confirm != pass -> "As senhas não coincidem."
            isRegister -> viewModel.register(name, email, pass)
            else -> viewModel.login(email, pass)
        }
        if (error != null) showError(error) else hideError()
    }

    private fun showError(message: String) {
        binding.loginView.tvLoginError.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun hideError() {
        binding.loginView.tvLoginError.visibility = View.GONE
    }

    private var wasLoggedIn = false

    private fun observeSession() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.settings.collect { settings ->
                    binding.loginContainer.visibility =
                        if (settings.loggedIn) View.GONE else View.VISIBLE
                    // Ao entrar (ou sair), sempre volta para a aba Hoje.
                    if (settings.loggedIn != wasLoggedIn) {
                        switchTab(Tab.HOJE)
                        wasLoggedIn = settings.loggedIn
                    }
                }
            }
        }
    }

    /** DM Sans no peso pedido (evita a normalização do AppCompat para 400). */
    private val dmSansFamily: Typeface? by lazy { ResourcesCompat.getFont(this, R.font.dm_sans) }
    private fun dmSans(weight: Int): Typeface? = dmSansFamily?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) Typeface.create(it, weight, false) else it
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let { imm.hideSoftInputFromWindow(it.windowToken, 0) }
    }
}
