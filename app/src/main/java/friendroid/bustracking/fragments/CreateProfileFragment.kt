package friendroid.bustracking.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import friendroid.bustracking.*
import friendroid.bustracking.activities.BusDriverActivity
import friendroid.bustracking.activities.RegistrationActivity


class CreateProfileFragment : Fragment() {
    private var v: View? = null
    private val CODE_CAMERA = 321
    private val CODE_GALLERY = 123

    private var profile_pic: CircleImageView? = null
    private var contact: AppCompatTextView? = null
    private var name_field: TextInputEditText? = null
    private var teacher: RadioButton? = null
    private var bus_driver: RadioButton? = null
    private var reg_key: TextInputEditText? = null
    private var progressBar: ProgressBar? = null
    private var next_button: MaterialButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_crteate_profile, container, false)

        profile_pic = v?.findViewById(R.id.profile_pic)
        contact = v?.findViewById(R.id.contact)
        name_field = v?.findViewById(R.id.name_field)
        teacher = v?.findViewById(R.id.teacher)
        bus_driver = v?.findViewById(R.id.bus_driver)
        reg_key = v?.findViewById(R.id.reg_key)
        progressBar = v?.findViewById(R.id.progressBar)
        next_button = v?.findViewById(R.id.next_button)

        profile_pic?.setOnClickListener {
            AlertDialog.Builder(activity).setTitle(R.string.take_pic_from).setItems(R.array.pic_options) { _, p ->
                if (p == 0) {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (activity?.packageManager?.let { takePictureIntent.resolveActivity(it) } != null) {
                        startActivityForResult(takePictureIntent, CODE_CAMERA)
                    }
                } else if (p == 1) {
                    val takePictureIntent = Intent(Intent.ACTION_GET_CONTENT)
                    takePictureIntent.type = "image/*"
                    if (activity?.packageManager?.let { takePictureIntent.resolveActivity(it) } != null) {
                        startActivityForResult(takePictureIntent, CODE_GALLERY)
                    }
                }
            }.create().show()
        }
        return v
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_CAMERA && resultCode == RESULT_OK) {
            profile_pic?.setImageBitmap(data?.extras?.get("data") as Bitmap)
        } else if (requestCode == CODE_GALLERY && resultCode == RESULT_OK) {
            profile_pic?.setImageURI(data?.data)
        }
    }

    private fun updateProfile() {
        if (cUser!!.displayName.toString() != name_field?.text?.trim().toString())
            cUser!!.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name_field?.text.toString()).build())
                    .addOnCompleteListener { }
        mUser.name = name_field?.text?.trim().toString()
        mUser.uid = cUser!!.uid
        if (cUser?.email != null) mUser.identity = contact?.text.toString()
        mUser.approved = false
        if (teacher?.isChecked == true) mUser.role = "teacher"
        else if (bus_driver?.isChecked == true) mUser.role = "driver"
        FirebaseFirestore.getInstance().also { it.firestoreSettings = fireSettings }.collection("users").document(mUser.uid).set(mUser).addOnSuccessListener {
            if (!isDetached) {
                if (mUser.role == "teacher") (activity as RegistrationActivity).showBusSelection()
                else {
                    startActivity(Intent(activity, BusDriverActivity::class.java)
                            .apply { putExtra(EXTRA_FIRST_READ, true) })
                    activity?.finish()
                }
            }
        }.addOnFailureListener {
            it.printStackTrace()
            activity?.finish()
        }
    }


    override fun onStart() {
        super.onStart()
        contact?.text = cUser?.email
        name_field?.setText(cUser!!.displayName.toString())
        next_button?.setOnClickListener {
            when {
                name_field?.text?.trim()?.isEmpty() == true -> {
                    name_field?.error = getString(R.string.enter_valid_name)
                    name_field?.requestFocus()
                }
                reg_key?.text?.isEmpty() == true -> {
                    reg_key?.error = getString(R.string.enter_reg_key)
                    reg_key?.requestFocus()
                }

                else -> {
                    it.isEnabled = false
                    progressBar?.visibility = View.VISIBLE

                    FirebaseFirestore.getInstance().collection("settings").document("secrets").get()
                            .addOnSuccessListener { documentSnapshot ->
                                if (reg_key?.text.toString() == documentSnapshot["reg_key"].toString()) {
                                    updateProfile()
                                } else {
                                    reg_key?.error = getString(R.string.invalid_key)
                                    reg_key?.requestFocus()

                                    it?.isEnabled = true
                                    progressBar?.visibility = View.INVISIBLE
                                }
                            }.addOnFailureListener { ex ->
                                if (!isDetached) alert(activity, "Oops! " + ex.message)
                                it?.isEnabled = true
                                progressBar?.visibility = View.INVISIBLE
                            }
                }
            }
        }
    }
}
