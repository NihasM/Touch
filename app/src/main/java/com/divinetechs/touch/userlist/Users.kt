package com.divinetechs.touch.userlist

class Users {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var profileImageUrl: String? = null // Add this property

    constructor(){}

    constructor(name: String?, email: String?, uid:String?){
        this.name = name
        this.email = email
        this.uid = uid
    }
}