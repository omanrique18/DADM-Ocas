package co.edu.unal.reto8

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.edu.unal.reto8.databinding.ItemEnterpriseBinding

class EnterpriseAdapter(
    private var enterprises: MutableList<EnterpriseEntity>,
    private var listener: OnClickListener
): RecyclerView.Adapter<EnterpriseAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    fun setEnterprises(enterprises: MutableList<EnterpriseEntity>) {
        this.enterprises = enterprises
        notifyDataSetChanged()
    }

    fun add(enterpriseEntity: EnterpriseEntity){
        if(!enterprises.contains(enterpriseEntity)) {
            enterprises.add(enterpriseEntity)
            notifyItemInserted(enterprises.size-1)
        }
    }

    fun delete(enterpriseEntity: EnterpriseEntity){
        val index = enterprises.indexOf(enterpriseEntity)
        if(index != -1){
            enterprises.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun update(enterpriseEntity: EnterpriseEntity){
        val index = enterprises.indexOf(enterpriseEntity)
        if(index != -1){
            enterprises[index] = enterpriseEntity
            notifyItemChanged(index)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_enterprise, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = enterprises.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val enterprise = enterprises[position]
        with(holder){
            setListener(enterprise)
            binding.enterpriseName.text = enterprise.name
            binding.enterpriseUrl.text = enterprise.url
            binding.enterprisePhone.text = enterprise.phone
            binding.enterpriseEmail.text = enterprise.email
            binding.enterpriseProductsServices.text = enterprise.productsAndServices
            binding.enterpriseClassification.text = enterprise.classification
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = ItemEnterpriseBinding.bind(view)

        fun setListener(enterpriseEntity: EnterpriseEntity){
            binding.root.setOnClickListener { listener.onClick(enterpriseEntity.id) }
            binding.root.setOnLongClickListener {
                listener.onDeleteEnterprise(enterpriseEntity)
                true
            }
        }
    }
}