package co.edu.unal.reto8

interface MainAux {
    fun hideAddButton(isVisible: Boolean = false)
    fun addEnterprise(enterpriseEntity: EnterpriseEntity)
    fun updateEnterprise(enterpriseEntity: EnterpriseEntity)
}