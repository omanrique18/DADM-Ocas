package co.edu.unal.reto8

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import co.edu.unal.reto8.databinding.FragmentEditEnterpriseBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import java.util.concurrent.LinkedBlockingQueue

class EditEnterpriseFragment : Fragment() {

    private lateinit var mBinding: FragmentEditEnterpriseBinding
    private var mActivity: MainActivity? = null
    private var mIsEditMode:Boolean = false
    private var mEnterpriseEntity: EnterpriseEntity? = null

    private fun hideKeyBoard(){
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun getEnterprise(id: Long){
        val queue = LinkedBlockingQueue<EnterpriseEntity?>()
        Thread{
            mEnterpriseEntity = EnterpriseApplication.dataBase.enterpriseDAO().getEnterpriseById(id)
            queue.add(mEnterpriseEntity)
        }.start()
        queue.take()?.let {
            setUiEnterprise(mEnterpriseEntity)
        }

    }

    private fun setUiEnterprise(enterpriseEntity: EnterpriseEntity?) {
        with(mBinding){
            eTextEnterpriseName.text = enterpriseEntity?.name?.editable()
            eTextEnterpriseEmail.text = enterpriseEntity?.email?.editable()
            eTextEnterprisePhone.text = enterpriseEntity?.phone?.editable()
            eTextEnterpriseUrl.text = enterpriseEntity?.url?.editable()
            eTextEnterpriseProdServ.text = enterpriseEntity?.productsAndServices?.editable()
        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun validateFields(vararg textFields: TextInputLayout): Boolean {
        var isValid = true
        for(textField in textFields){
            if(textField.editText?.text.toString().trim().isEmpty()) {
                textField.error = getString(R.string.helper_required)
                isValid = false
            }else {
                textField.error = null
            }
        }
        return isValid
    }

    private fun setupTextFields(){
        with(mBinding){
            eTextEnterpriseName.addTextChangedListener { validateFields(tilEnterpriseName) }
            eTextEnterprisePhone.addTextChangedListener { validateFields(tilEnterprisePhone) }
            eTextEnterpriseEmail.addTextChangedListener { validateFields(tilEnterpriseEmail) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentEditEnterpriseBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getLong(getString(R.string.args_id),0)
        if(id != null && id != 0L) {
            mIsEditMode = true
            getEnterprise(id)
        }else{
            mIsEditMode = false
            mEnterpriseEntity = EnterpriseEntity(
                name = "",
                url = "",
                phone = "",
                email = "",
                productsAndServices = "",
                classification = "")

        }
        setupActionBar()
        setHasOptionsMenu(true)
        setupTextFields()
    }

    private fun setupActionBar(){
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title =
            if(mIsEditMode) getString(R.string.edit_enterprise_title_edit)
            else getString(R.string.edit_enterprise_title_add)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add_enterprise, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                mActivity?.onBackPressedDispatcher?.onBackPressed()
                true
            }
            R.id.action_save -> {
                if(mEnterpriseEntity != null && validateFields(mBinding.tilEnterpriseEmail,
                        mBinding.tilEnterprisePhone,
                        mBinding.tilEnterpriseName)) {
                    with(mEnterpriseEntity!!) {
                        name = mBinding.eTextEnterpriseName.text.toString().trim()
                        url = mBinding.eTextEnterpriseUrl.text.toString().trim()
                        phone = mBinding.eTextEnterprisePhone.text.toString().trim()
                        email = mBinding.eTextEnterpriseEmail.text.toString().trim()
                        productsAndServices =
                            mBinding.eTextEnterpriseProdServ.text.toString().trim()
                        classification = "TODO()"
                    }

                    val queue = LinkedBlockingQueue<EnterpriseEntity>()
                    Thread {
                        if(mIsEditMode) {
                            EnterpriseApplication.dataBase.enterpriseDAO()
                                .updateEnterprise(mEnterpriseEntity!!)
                        }else{
                            mEnterpriseEntity!!.id = EnterpriseApplication.dataBase.enterpriseDAO()
                            .addEnterprise(mEnterpriseEntity!!)
                        }
                        queue.add(mEnterpriseEntity)
                    }.start()
                    with(queue.take()) {
                        if(mIsEditMode){
                            mActivity?.updateEnterprise(this)
                            Toast.makeText(
                                mActivity,
                                R.string.edit_enterprise_message_update_success,
                                Toast.LENGTH_LONG)
                                .show()

                        }else {
                            mActivity?.addEnterprise(this)
                            Toast.makeText(
                                mActivity,
                                R.string.edit_enterprise_message_add_success,
                                Toast.LENGTH_LONG)
                                .show()
                        }
                        hideKeyBoard()
                        mActivity?.onBackPressedDispatcher?.onBackPressed()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        hideKeyBoard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideAddButton(true)
        setHasOptionsMenu(false)
        super.onDestroy()
    }
}