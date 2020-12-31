package backend21.domain.alert

interface Alert {
    fun getExtravars():HashMap<String, String>
    fun getAlertAlias():String
    fun requiresEmail():Boolean

}
