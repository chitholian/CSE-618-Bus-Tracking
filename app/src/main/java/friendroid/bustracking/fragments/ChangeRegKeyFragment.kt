package friendroid.bustracking.fragments

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import friendroid.bustracking.R
import friendroid.bustracking.activities.BaseActivity
import kotlinx.android.synthetic.main.dialog_input_reg.*

class ChangeRegKeyFragment : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_input_reg, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<AppCompatButton>(R.id.cancel_button)?.setOnClickListener {
            dismiss()
        }
        view.findViewById<AppCompatButton>(R.id.ok_button)?.setOnClickListener {
            if (reg_key_field.text.isEmpty()) reg_key_field.error = getString(R.string.enter_reg_key)
            else {
                progressBar?.visibility = View.VISIBLE
                (activity as BaseActivity).apply {
                    delayed {
                        toast(R.string.key_changed)
                        dialog.dismiss()
                    }
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also {
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
    }
}
