package co.edu.unal.reto8

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import co.edu.unal.reto8.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.concurrent.LinkedBlockingQueue

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: EnterpriseAdapter
    private lateinit var mGridLayout: GridLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.addButton.setOnClickListener { launchEditEnterpriseFragment() }
        setupRecyclerView()
    }

    private fun launchEditEnterpriseFragment(args: Bundle? = null) {
        val fragment = EditEnterpriseFragment()
        if(args!=null) fragment.arguments = args

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        hideAddButton()
    }

    private fun setupRecyclerView() {
        mAdapter = EnterpriseAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, 1)
        getEnterprises()

        mBinding.recyclerView.apply {
            setHasFixedSize(false)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun getEnterprises(){
        val queue = LinkedBlockingQueue<MutableList<EnterpriseEntity>>()
        Thread{
            val enterprises = EnterpriseApplication.dataBase.enterpriseDAO().getAllEnterprises()
            queue.add(enterprises)
        }.start()

        mAdapter.setEnterprises(queue.take())
    }

    override fun onClick(storeId: Long) {
        val args = Bundle()
        args.putLong(getString(R.string.args_id), storeId)
        launchEditEnterpriseFragment(args)
    }

    override fun onDeleteEnterprise(enterpriseEntity: EnterpriseEntity) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm) { _, _ ->
                val queue = LinkedBlockingQueue<EnterpriseEntity>()
                Thread {
                    EnterpriseApplication.dataBase.enterpriseDAO()
                        .deleteEnterprise(enterpriseEntity)
                    queue.add(enterpriseEntity)
                }.start()
                mAdapter.delete(enterpriseEntity)
            }
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()
    }

    override fun hideAddButton(isVisible: Boolean) {
        if(isVisible) mBinding.addButton.show() else mBinding.addButton.hide()
    }

    override fun addEnterprise(enterpriseEntity: EnterpriseEntity) {
        mAdapter.add(enterpriseEntity)
    }

    override fun updateEnterprise(enterpriseEntity: EnterpriseEntity) {
        mAdapter.update(enterpriseEntity)
    }
}