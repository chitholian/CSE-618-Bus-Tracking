package friendroid.bustracking.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.UserProfileChangeRequest
import de.hdodenhof.circleimageview.CircleImageView
import friendroid.bustracking.R
import friendroid.bustracking.activities.BaseActivity
import friendroid.bustracking.activities.BusDriverActivity
import friendroid.bustracking.activities.RegistrationActivity
import friendroid.bustracking.utils.alert
import friendroid.bustracking.utils.cDatabaseRef
import friendroid.bustracking.utils.cUser
import kotlinx.android.synthetic.main.fragment_crteate_profile.*


class CreateProfileFragment : Fragment() {
    private var v: View? = null
    private val CODE_CAMERA = 321
    private val CODE_GALLERY = 123

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        v = inflater.inflate(R.layout.fragment_crteate_profile, container, false)
        v?.findViewById<View>(R.id.next_button)?.setOnClickListener {
            when {
                name_field.text.trim().isEmpty() -> {
                    name_field.error = getString(R.string.enter_valid_name)
                    name_field.requestFocus()
                }
                reg_key.text.isEmpty() -> {
                    reg_key.error = getString(R.string.enter_reg_key)
                    reg_key.requestFocus()
                }

                else -> {
                    (activity as BaseActivity).delayed {
                        if (teacher.isChecked) (activity as RegistrationActivity).showBusSelection()
                        else {
                            startActivity(Intent(activity, BusDriverActivity::class.java))
                            activity?.finish()
                        }
                    }
                    /*cDatabaseRef.child("reg_key").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (reg_key.text.toString().equals(p0.value.toString())) {
                                updateProfile()
                            } else {
                                reg_key.error = p0.value.toString() //getString(R.string.invalid_key)
                                reg_key.requestFocus()
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            alert(activity, p0.message)
                        }
                    })*/
                }
            }
        }
        v?.findViewById<CircleImageView>(R.id.profile_pic)?.setOnClickListener {
            AlertDialog.Builder(activity).setTitle(R.string.take_pic_from).setItems(R.array.pic_options) { _, p ->
                if (p == 0) {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePictureIntent.resolveActivity(activity?.packageManager) != null) {
                        startActivityForResult(takePictureIntent, CODE_CAMERA)
                    }
                } else if (p == 1) {
                    val takePictureIntent = Intent(Intent.ACTION_GET_CONTENT)
                    takePictureIntent.type = "image/*"
                    if (takePictureIntent.resolveActivity(activity?.packageManager) != null) {
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
            profile_pic.setImageBitmap(data?.extras?.get("data") as Bitmap)
        } else if (requestCode == CODE_GALLERY && resultCode == RESULT_OK) {
            profile_pic.setImageURI(data?.data)
        }
    }

    private fun updateProfile() {
        if (cUser!!.displayName.toString() != name_field.text.trim().toString())
            cUser!!.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name_field.text.toString()).build())
                    .addOnCompleteListener { setRole() }
        else setRole()
    }

    private fun setRole() {
        when {
            teacher.isChecked -> {
                cDatabaseRef.child("users").child(cUser!!.uid).child("role").setValue("teacher")
                        .addOnSuccessListener {
                            (activity as RegistrationActivity).showBusSelection()
                        }.addOnCanceledListener { alert(activity, R.string.op_failed) }
            }
            bus_driver.isChecked -> {
                cDatabaseRef.child("users").child(cUser!!.uid).child("role").setValue("driver")
                        .addOnSuccessListener {
                            startActivity(Intent(activity, BusDriverActivity::class.java))
                        }.addOnCanceledListener { alert(activity, R.string.op_failed) }
            }
        }
    }

    /*override fun onStart() {
        super.onStart()
        name_field.setText(cUser!!.displayName.toString())
    }*/
}
