package com.example.campusconnectfinal.adapters

class ModelAd {

    var id: String = ""
    var uid: String = ""
    var price: String = ""
    var title: String = ""
    var des: String = ""
    var used: String = ""

    constructor()

    constructor(id : String, uid: String, price: String,title: String, des: String, used: String ){
        this.id = id
        this.uid = uid
        this.title = title
        this.des = des
        this.price = price
        this.used = used
    }
}