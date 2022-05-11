package com.eronka.fincan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.login_password_txt
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth= FirebaseAuth.getInstance()
    }

    fun signUp(view: View) {
        val email=register_email_txt.text.toString()
        val password=login_password_txt.text.toString()
        val password2=register_password_txt2.text.toString()

        if(password==password2 && email!="" && password!="" && password2!=""){
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {

                if (it.isSuccessful){
                    val intent= Intent(this,HomepageActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }

        }
        else{
            Toast.makeText(this,"Lütfen Bilgilerinizi Eksiksiz ve Doğru bir şekilde giriniz..",Toast.LENGTH_LONG).show()
        }
    }
    fun geriDon(view: View){
        finish()

    }
    fun goLogin(view: View){
        startActivity(Intent(this,LoginActivity::class.java))
        finish()
    }
}