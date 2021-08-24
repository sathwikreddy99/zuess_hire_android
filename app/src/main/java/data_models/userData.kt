package data_models

data class userData(
    var firstName: String,
    var lastName: String,
    var profilePhoto : String,
    var jobTitles : Any?,
    var rating : Any?,
    var servicesOffered : List<Map<String,Any>>,
    var price : Any?,
)