package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEt: EditText
    private lateinit var full_nameEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var textPhoneNumberPatientEt: EditText
    private lateinit var textAddressPatientEt: EditText
    private lateinit var textPhoneNumberPatient2Et: EditText
    private lateinit var textPhoneNumberPatient3Et: EditText

    private lateinit var rolePatientTX: TextView
    private lateinit var textPatientFullNameTX: TextView
    private lateinit var textPhoneNumberPatientTX: TextView
    private lateinit var textAddressPatientTX: TextView
    private lateinit var textPhoneNumberPatient2TX: TextView
    private lateinit var textPhoneNumberPatient3TX: TextView

    private lateinit var symtompsTextInput: TextInputEditText
    private lateinit var allergiesTextInput: TextInputEditText
    private lateinit var deseasesTextInput: TextInputEditText
    private lateinit var partOfTheBodyTextInput: TextInputEditText

    private lateinit var listViewView: ListView
    private val directoryEntries =
        ArrayList<String>()
    private lateinit var database: DatabaseReference
    val myDB = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.login)
        button.setOnClickListener {
            var email: String = Allergies.text.toString()
            var password: String = deseases.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this@MainActivity, "Please fill all the fields", Toast.LENGTH_LONG)
                    .show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show()
                            setContentView(R.layout.activity_profile)
                            textPhoneNumberPatientTX = findViewById(R.id.textPhoneNumberPatient)
                            textAddressPatientTX = findViewById(R.id.textAddressPatient)
                            textPhoneNumberPatient2TX =
                                findViewById(R.id.textPhoneNumberPatient2)
                            textPhoneNumberPatient3TX =
                                findViewById(R.id.textPhoneNumberPatient3)
                            rolePatientTX = findViewById(R.id.rolePatient)
                            textPatientFullNameTX = findViewById(R.id.textPatientFullName)
                            val buttonAddition = findViewById<Button>(R.id.additional_info)
                            myDB.collection("users")
                                .whereEqualTo("email", auth.currentUser?.email)
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        textPhoneNumberPatientTX.text =
                                            document.data.get("phoneNumber").toString()
                                        textAddressPatientTX.text =
                                            document.data.get("address").toString()
                                        textPhoneNumberPatient2TX.text =
                                            document.data.get("email").toString()
                                        textPhoneNumberPatient3TX.text =
                                            document.data.get("iin").toString()
                                        textPatientFullNameTX.text =
                                            document.data.get("full_name").toString()
                                        if (document.data.containsKey("role") && document.data.get("role") == "ROLE_DOCTOR") {
                                            rolePatientTX.text =
                                                document.data.get("role").toString();
                                            buttonAddition.setVisibility(View.GONE);
                                        }
                                        if (document.data.containsKey("role") && document.data.get("role") == "ROLE_ADMIN") {
                                            rolePatientTX.text =
                                                document.data.get("role").toString();
                                            buttonAddition.setVisibility(View.GONE);
                                        }
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.w("", "Error getting documents: ", exception)
                                }
                            val myTicketsButton = findViewById<Button>(R.id.login3)
                            myTicketsButton.setOnClickListener {

                                setContentView(R.layout.acitivity_main7)
                                listViewView = findViewById(R.id.ListView)
                                myDB.collection("tickets")
                                    .whereEqualTo("ticket", true)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            if (auth.currentUser?.email == document.data["email_patient"]) {
                                                val textString: String =
                                                    document.data["email_patient"].toString()
                                                val textString2: String =
                                                    document.data["appointment"].toString()
                                                val textString3: String =
                                                    document.data["email_doctor"].toString()
                                                directoryEntries.add(
                                                    "Your appoint is on doctor with email $textString3 check your email $textString, Appointment type:$textString2"
                                                );
                                            }
                                            if (auth.currentUser?.email == document.data["email_doctor"]) {
                                                val textString: String =
                                                    document.data["email_patient"].toString()
                                                val textString2: String =
                                                    document.data["appointment"].toString()
                                                val textString3: String =
                                                    document.data["email_doctor"].toString()
                                                directoryEntries.add(
                                                    "Your patient is email $textString please write on email $textString3, Appointment type:$textString2"
                                                );
                                            }

                                            val adapter =
                                                ArrayAdapter(
                                                    this,
                                                    android.R.layout.simple_list_item_1,
                                                    directoryEntries
                                                )

                                            listViewView.setAdapter(adapter)
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.w("", "Error getting documents: ", exception)
                                    }

// 2

                            }

                            val additional_info = findViewById<Button>(R.id.additional_info)
                            additional_info.setOnClickListener {
                                setContentView(R.layout.activity_main3)
                                symtompsTextInput = findViewById(R.id.symptoms)
                                allergiesTextInput = findViewById(R.id.Allergies)
                                deseasesTextInput = findViewById(R.id.deseases)
                                partOfTheBodyTextInput = findViewById(R.id.partOfTheBody)
                                val finishButton = findViewById<Button>(R.id.finish)
                                finishButton.setOnClickListener {
                                    var sympto = symtompsTextInput.text.toString()
                                    var allergo = allergiesTextInput.text.toString()
                                    var deaseso = deseasesTextInput.text.toString()
                                    var partBody = partOfTheBodyTextInput.text.toString()
                                    myDB.collection("users")
                                        .whereEqualTo("email", auth.currentUser?.email)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            for (document in documents) {
                                                myDB.collection("users").document(document.id)
                                                    .update("symptoms", sympto)
                                                myDB.collection("users").document(document.id)
                                                    .update("deseases", deaseso)
                                                myDB.collection("users").document(document.id)
                                                    .update("allergies", allergo)
                                                myDB.collection("users").document(document.id)
                                                    .update("partOfTheBody", partBody)
                                            }
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.w("", "Error getting documents: ", exception)
                                        }
                                    setContentView(R.layout.activity_profile)
                                    textPhoneNumberPatientTX =
                                        findViewById(R.id.textPhoneNumberPatient)
                                    textAddressPatientTX = findViewById(R.id.textAddressPatient)
                                    textPhoneNumberPatient2TX =
                                        findViewById(R.id.textPhoneNumberPatient2)
                                    textPhoneNumberPatient3TX =
                                        findViewById(R.id.textPhoneNumberPatient3)
                                    textPatientFullNameTX = findViewById(R.id.textPatientFullName)
                                    myDB.collection("users")
                                        .whereEqualTo("email", auth.currentUser?.email)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            for (document in documents) {
                                                textPhoneNumberPatientTX.text =
                                                    document.data.get("phoneNumber").toString()
                                                textAddressPatientTX.text =
                                                    document.data.get("address").toString()
                                                textPhoneNumberPatient2TX.text =
                                                    document.data.get("email").toString()
                                                textPhoneNumberPatient3TX.text =
                                                    document.data.get("iin").toString()
                                                textPatientFullNameTX.text =
                                                    document.data.get("full_name").toString()
                                                if (document.data.containsKey("role") && document.data.get(
                                                        "role"
                                                    ) == "ROLE_DOCTOR"
                                                ) {
                                                    rolePatientTX.text =
                                                        document.data.get("role").toString();
                                                }
                                                if (document.data.containsKey("role") && document.data.get(
                                                        "role"
                                                    ) == "ROLE_ADMIN"
                                                ) {
                                                    rolePatientTX.text =
                                                        document.data.get("role").toString();
                                                }
                                            }
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.w("", "Error getting documents: ", exception)
                                        }

                                    val logout2 = findViewById<Button>(R.id.Logout)
                                    logout2.setOnClickListener {
                                        if (auth.currentUser != null) {
                                            setContentView(R.layout.activity_main)

                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Already logged in",
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                        }
                                    }
                                }
                            }
                            val logout = findViewById<Button>(R.id.Logout)
                            logout.setOnClickListener {
                                if (auth.currentUser != null) {
                                    setContentView(R.layout.activity_main)
                                } else {
                                    Toast.makeText(this, "Already logged in", Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            val edit = findViewById<Button>(R.id.login2)

                            myDB.collection("users")
                                .whereEqualTo("email", auth.currentUser?.email)
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        if (document.data.containsKey("role") && document.data.get(
                                                "role"
                                            ) == "ROLE_ADMIN"
                                        ) {
                                            edit.setOnClickListener {
                                                setContentView(R.layout.activity_main2)
                                            }
                                        }


                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.w("", "Error getting documents: ", exception)
                                }

                            edit.setOnClickListener {
                                setContentView(R.layout.activity_profile_edit)
                                val save = findViewById<Button>(R.id.Cancel)
                                textPhoneNumberPatientEt = findViewById(R.id.textPhoneNumberPatient)
                                textAddressPatientEt = findViewById(R.id.textAddressPatient)
                                textPhoneNumberPatient2Et =
                                    findViewById(R.id.textPhoneNumberPatient2)
                                textPhoneNumberPatient3Et =
                                    findViewById(R.id.textPhoneNumberPatient3)
                                save.setOnClickListener {
                                    var phoneNumber = textPhoneNumberPatientEt.text.toString()
                                    var address = textAddressPatientEt.text.toString()
                                    var email_edit = textPhoneNumberPatient2Et.text.toString()
                                    var iin = textPhoneNumberPatient3Et.text.toString()

                                    myDB.collection("users")
                                        .whereEqualTo("email", auth.currentUser?.email)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            for (document in documents) {
                                                myDB.collection("users").document(document.id)
                                                    .update("email", email_edit)
                                                myDB.collection("users").document(document.id)
                                                    .update("address", address)
                                                myDB.collection("users").document(document.id)
                                                    .update("phoneNumber", phoneNumber)
                                                myDB.collection("users").document(document.id)
                                                    .update("iin", iin)
                                                auth.currentUser?.updateEmail(email_edit)
                                            }
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.w("", "Error getting documents: ", exception)
                                        }

                                    setContentView(R.layout.activity_profile)
                                    textPhoneNumberPatientTX =
                                        findViewById(R.id.textPhoneNumberPatient)
                                    textAddressPatientTX = findViewById(R.id.textAddressPatient)
                                    textPhoneNumberPatient2TX =
                                        findViewById(R.id.textPhoneNumberPatient2)
                                    textPhoneNumberPatient3TX =
                                        findViewById(R.id.textPhoneNumberPatient3)
                                    textPatientFullNameTX = findViewById(R.id.textPatientFullName)
                                    myDB.collection("users")
                                        .whereEqualTo("email", auth.currentUser?.email)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            for (document in documents) {
                                                textPhoneNumberPatientTX.text =
                                                    document.data.get("phoneNumber").toString()
                                                textAddressPatientTX.text =
                                                    document.data.get("address").toString()
                                                textPhoneNumberPatient2TX.text =
                                                    document.data.get("email").toString()
                                                textPhoneNumberPatient3TX.text =
                                                    document.data.get("iin").toString()
                                                textPatientFullNameTX.text =
                                                    document.data.get("full_name").toString()
                                                if (document.data.containsKey("role") && document.data.get(
                                                        "role"
                                                    ) == "ROLE_DOCTOR"
                                                ) {
                                                    rolePatientTX.text =
                                                        document.data.get("role").toString();
                                                }
                                                if (document.data.containsKey("role") && document.data.get(
                                                        "role"
                                                    ) == "ROLE_ADMIN"
                                                ) {
                                                    rolePatientTX.text =
                                                        document.data.get("role").toString();
                                                }
                                            }
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.w("", "Error getting documents: ", exception)
                                        }
                                    val logout2 = findViewById<Button>(R.id.Logout)
                                    logout2.setOnClickListener {
                                        if (auth.currentUser != null) {
                                            setContentView(R.layout.activity_main)
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Already logged in",
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                        }
                                    }
                                }

                            }

                        } else {
                            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
                        }
                    })
            }
        }
        val textView = findViewById<TextView>(R.id.textViewRegister)
        textView.setOnClickListener {
            setContentView(R.layout.activity_main2)
            val buttonRegister = findViewById<Button>(R.id.Register)
            buttonRegister.setOnClickListener {

                emailEt = findViewById(R.id.Allergies)
                passwordEt = findViewById(R.id.deseases)
                full_nameEt = findViewById(R.id.symptoms)

                buttonRegister.setOnClickListener {
                    var email: String = emailEt.text.toString()
                    var password: String = passwordEt.text.toString()
                    var full_name: String = full_nameEt.text.toString()
                    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                        Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, OnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    val user: MutableMap<String, Any> =
                                        HashMap()
                                    user["full_name"] = full_name
                                    user["email"] = email
                                    user["password"] = password

                                    myDB.collection("users")
                                        .add(user)
                                        .addOnSuccessListener(OnSuccessListener<DocumentReference> { documentReference ->
                                            Toast.makeText(
                                                this,
                                                "Successfully Registered",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        })
                                        .addOnFailureListener(OnFailureListener { e ->
                                            Toast.makeText(
                                                this,
                                                "Not Registered",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        })

                                    Toast.makeText(
                                        this,
                                        "Successfully Registered",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Registration Failed",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            })
                    }
                }
                // setContentView(R.layout.activity_main3)

                /*val textViewLogin = findViewById<TextView>(R.id.textViewLogin)
                textViewLogin.setOnClickListener {
                    setContentView(R.layout.activity_main)
                }

                    val buttonFinish = findViewById<Button>(R.id.finish)
                    buttonFinish.setOnClickListener {
                        setContentView(R.layout.activity_profile)
                        val buttonMyTicket = findViewById<Button>(R.id.login3)
                        buttonMyTicket.setOnClickListener {
                            setContentView(R.layout.activity_main6)
                        }
                        val editButton = findViewById<Button>(R.id.login2)
                        editButton.setOnClickListener {
                            setContentView(R.layout.activity_profile_edit)
                            val cancelButton = findViewById<Button>(R.id.Cancel)
                            cancelButton.setOnClickListener {
                                setContentView(R.layout.activity_profile)
                                val myticketsbutton = findViewById<Button>(R.id.login3)
                                myticketsbutton.setOnClickListener{
                                    setContentView(R.layout.activity_main6)
                                }

                            }

                        }
                    }*/


            }
        }


    }


}
