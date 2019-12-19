package com.eduardo.economise

class Lancamento (val id: String, val desc: String, val valor: String, val data: String, val categoria: String, val usuario: String) {

    constructor() : this ("","", "", "", "", "") {

    }

}