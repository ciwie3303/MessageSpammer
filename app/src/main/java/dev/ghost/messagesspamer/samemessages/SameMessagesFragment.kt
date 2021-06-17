package dev.ghost.messagesspamer.samemessages

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dev.ghost.messagesspamer.FileWorker
import dev.ghost.messagesspamer.UserPrefs
import dev.ghost.messagesspamer.databinding.FragmentSameMessagesBinding

/**
 * A simple [Fragment] subclass.
 * Use the [SameMessagesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SameMessagesFragment : Fragment() {

    lateinit var binding: FragmentSameMessagesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSameMessagesBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.phonesText.setText(UserPrefs.phones)
        binding.messageText.setText(UserPrefs.message)

        binding.chooseFileWithNumbersButton.setOnClickListener {
            requestPermissionForReadFile()
        }

        binding.messageText.addTextChangedListener {
            UserPrefs.message = it.toString()
        }

        binding.phonesText.addTextChangedListener {
            UserPrefs.phones = it.toString()
        }

        binding.sendButton.setOnClickListener {
            requestPermissionForSmsSending()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SameMessagesFragment.
         */

        fun newInstance() =
            SameMessagesFragment().apply {

            }
    }


    private fun requestPermissionForReadFile() {
        Dexter.withContext(this.activity)
            .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    showMessage("Доступ получен")
                    chooseFile()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    showMessage("Отказано")
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    showMessage("WTF man?")
                    p1?.continuePermissionRequest()
                }

            })
            .check();
    }

    private fun requestPermissionForSmsSending() {
        Dexter.withContext(this@SameMessagesFragment.activity)
            .withPermission(android.Manifest.permission.SEND_SMS)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    showMessage("Доступ получен")
                    sendMessages()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    showMessage("Отказано")
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    showMessage("WTF man?")
                    p1?.continuePermissionRequest()
                }

            })
            .check();
    }

    private fun chooseFile() {
        showFileChooser(FileWorker.FILE_SELECT_SAME_CODE)
    }

    private fun showFileChooser(selectedCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            startActivityForResult(
                Intent.createChooser(intent, "Выберите файл для загрузки"),
                selectedCode
            )

        } catch (ex: ActivityNotFoundException) {
            // Potentially direct the user to the Market with a Dialog
            showMessage("Необходим файловый менеджер.")
        }
    }

    private fun sendMessages() {
        var goodSent = 0

        val phonesArray = UserPrefs.phones.split(',')
        for (phone in phonesArray) {
            val currentPhone = phone.trim()
            try {
                val smsManager: SmsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(
                    currentPhone,
                    null,
                    UserPrefs.message,
                    null,
                    null
                )
                goodSent++
                Log.d("SMS_SENDING", "Message sent on $currentPhone")
            } catch (ex: Exception) {
                Log.d("SMS_SENDING", "Exception on $currentPhone")
                showMessage("Ошибка при отправке на номер $currentPhone (${ex.message.toString()})")
            }
        }
        showMessage("Сообщения отправлены успешно на $goodSent номера из ${phonesArray.count()}")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            FileWorker.FILE_SELECT_SAME_CODE -> if (resultCode == AppCompatActivity.RESULT_OK) {
                // Get the Uri of the selected file
                data?.data?.let {
                    val uri: Uri = it

                    Log.d("FILE_CHOOSING", "File Uri: " + uri.toString())

                    val data = FileWorker.openFile(uri, this@SameMessagesFragment.activity!!)
                    if (data.isNotEmpty()) {
                        binding.phonesText.setText(data)
                        showMessage("Данные получены.")
                    } else
                        showMessage("Не удалось считать данные из файла. Повторите попытку.")
                }

            }
        }
    }

    fun showMessage(message: String) {
        Toast.makeText(
            this.context,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}