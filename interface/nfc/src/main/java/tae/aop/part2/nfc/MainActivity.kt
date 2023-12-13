package tae.aop.part2.nfc

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.provider.Settings
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import tae.aop.part2.nfc.record.ParsedNdefRecord
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = "NFCActivity"
    private var tagList: LinearLayout? = null
    private var nfcAdapter: NfcAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate 호출됨")
        setContentView(R.layout.activity_main)
        tagList = findViewById<View>(R.id.list) as LinearLayout
        // NFC 인텐트 처리
        if (intent.action == "nfc_MainActivity") {
            intentFromAPP(intent)
        }
        else if (intent.action == "android.intent.action.MAIN"){
            resolveIntent(intent)
        }
        else{
            finish()
        }
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            showNoNfcDialog()
            return
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume 호출됨")
        if (nfcAdapter?.isEnabled == false) {
            openNfcSettings()
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent_Mutable
        )
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause 호출됨")
        nfcAdapter?.disableForegroundDispatch(this)
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent 호출됨, Intent: $intent")
        setIntent(intent)
        if (intent.action in listOf(NfcAdapter.ACTION_TAG_DISCOVERED, NfcAdapter.ACTION_TECH_DISCOVERED, NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            intentFromAPP(intent)
        }
        finish()
    }

    private fun showNoNfcDialog() {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.no_nfc)
            .setNeutralButton(R.string.close_app) { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun openNfcSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent(Settings.Panel.ACTION_NFC)
        } else {
            Intent(Settings.ACTION_WIRELESS_SETTINGS)
        }
        startActivity(intent)
    }

    private fun resolveIntent(intent: Intent) {
        Log.d(TAG, "resolveIntent 호출됨, Intent: $intent")
        var IDdecString: String = "myhome383"
        val validActions = listOf(
            NfcAdapter.ACTION_TAG_DISCOVERED,
            NfcAdapter.ACTION_TECH_DISCOVERED,
            NfcAdapter.ACTION_NDEF_DISCOVERED
        )
        if (intent.action in validActions) {
            // TODO
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val messages = mutableListOf<NdefMessage>()
            if (rawMsgs != null) {
                rawMsgs.forEach {
                    messages.add(it as NdefMessage)
                }
            } else {
                // Unknown tag type
                val empty = ByteArray(0)
                Log.d("NFCinfo", empty.toString() )
                val id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
                IDdecString = if (id != null) toDec(id).toString() else "NULL"
                Log.d("NFCinfo", IDdecString )
                val tag = intent.parcelable<Tag>(NfcAdapter.EXTRA_TAG) ?: return
                Log.d("NFCinfo", tag.toString() )
                val payload = dumpTagData(tag).toByteArray()
                Log.d("NFCinfo", payload.toString() )
                val record = NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload)
                Log.d("NFCinfo", record.id.toString() )
                val msg = NdefMessage(arrayOf(record))
                Log.d("NFCinfo", msg.toString() )
                messages.add(msg)

            }

            // Setup the views
            buildTagViews(messages)
            var db: FirebaseFirestore = FirebaseFirestore.getInstance()

            var usingId:String = "mylandy2" // TODO 회원가입 기능 추가 후 해당하는 ID 갱신하도록 수정

            db.collection("SerialNumber").document(IDdecString).update("UsingID", usingId)
        }
    }

    // APP 모듈 인텐트 처리
    private fun intentFromAPP(intent: Intent) {
        Log.d(TAG, "intentFromAPP 호출됨, Intent: $intent")
        var IDdecString: String = "myhome383"

        // APP 모듈과의 인터페이스 관련 로직
        val id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
        IDdecString = if (id != null) toDec(id).toString() else "NULL"

        // App 모듈로 결과 전달
        val returnIntent = Intent()
        returnIntent.action = "ACTION_ALREADY_HANDLED"
        returnIntent.putExtra("DispenserID", IDdecString)
        returnIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        setResult(Activity.RESULT_OK, returnIntent)
        finish() // NFC 모듈 액티비티 종료
    }

    private fun dumpTagData(tag: Tag): String {
        val sb = StringBuilder()
        val id = tag.id
        sb.append("ID (hex): ").append(toHex(id)).append('\n')
        sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n')
        sb.append("ID (dec): ").append(toDec(id)).append('\n')
        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n')
        val prefix = "android.nfc.tech."
        sb.append("Technologies: ")
        for (tech in tag.techList) {
            sb.append(tech.substring(prefix.length))
            sb.append(", ")
        }
        sb.delete(sb.length - 2, sb.length)
        for (tech in tag.techList) {
            if (tech == MifareClassic::class.java.name) {
                sb.append('\n')
                var type = "Unknown"
                try {
                    val mifareTag = MifareClassic.get(tag)

                    when (mifareTag.type) {
                        MifareClassic.TYPE_CLASSIC -> type = "Classic"
                        MifareClassic.TYPE_PLUS -> type = "Plus"
                        MifareClassic.TYPE_PRO -> type = "Pro"
                    }
                    sb.appendLine("Mifare Classic type: $type")
                    sb.appendLine("Mifare size: ${mifareTag.size} bytes")
                    sb.appendLine("Mifare sectors: ${mifareTag.sectorCount}")
                    sb.appendLine("Mifare blocks: ${mifareTag.blockCount}")
                } catch (e: Exception) {
                    sb.appendLine("Mifare classic error: ${e.message}")
                }
            }
            if (tech == MifareUltralight::class.java.name) {
                sb.append('\n')
                val mifareUlTag = MifareUltralight.get(tag)
                var type = "Unknown"
                when (mifareUlTag.type) {
                    MifareUltralight.TYPE_ULTRALIGHT -> type = "Ultralight"
                    MifareUltralight.TYPE_ULTRALIGHT_C -> type = "Ultralight C"
                }
                sb.append("Mifare Ultralight type: ")
                sb.append(type)
            }
        }
        return sb.toString()
    }

    private fun toHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in bytes.indices.reversed()) {
            val b = bytes[i].toInt() and 0xff
            if (b < 0x10) sb.append('0')
            sb.append(Integer.toHexString(b))
            if (i > 0) {
                sb.append(" ")
            }
        }
        return sb.toString()
    }

    private fun toReversedHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in bytes.indices) {
            if (i > 0) {
                sb.append(" ")
            }
            val b = bytes[i].toInt() and 0xff
            if (b < 0x10) sb.append('0')
            sb.append(Integer.toHexString(b))
        }
        return sb.toString()
    }

    private fun toDec(bytes: ByteArray): Long {
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices) {
            val value = bytes[i].toLong() and 0xffL
            result += value * factor
            factor *= 256L
        }
        return result
    }

    private fun toReversedDec(bytes: ByteArray): Long {
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices.reversed()) {
            val value = bytes[i].toLong() and 0xffL
            result += value * factor
            factor *= 256L
        }
        return result
    }

    private fun buildTagViews(msgs: List<NdefMessage>) {
        if (msgs.isEmpty()) {
            return
        }
        val inflater = LayoutInflater.from(this)
        val content = tagList

        // Parse the first message in the list
        // Build views for all of the sub records
        val now = Date()
        val records = NdefMessageParser.parse(msgs[0])
        val size = records.size
        for (i in 0 until size) {
            val timeView = TextView(this)
            timeView.text = TIME_FORMAT.format(now)
            content!!.addView(timeView, 0)
            val record: ParsedNdefRecord = records[i]
            content.addView(record.getView(this, inflater, content, i), 1 + i)
            content.addView(inflater.inflate(R.layout.tag_divider, content, false), 2 + i)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_main_clear -> {
                clearTags()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clearTags() {
        for (i in tagList!!.childCount - 1 downTo 0) {
            val view = tagList!!.getChildAt(i)
            if (view.id != R.id.tag_viewer_text) {
                tagList!!.removeViewAt(i)
            }
        }
    }

    companion object {
        private val TIME_FORMAT = SimpleDateFormat.getDateTimeInstance()
    }
}