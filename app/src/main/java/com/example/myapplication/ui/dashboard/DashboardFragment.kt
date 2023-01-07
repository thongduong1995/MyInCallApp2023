package com.example.myapplication.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapplication.databinding.FragmentDashboardBinding
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        readCallLog()
        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("Range")
    private fun readCallLog()
    {
        val sortOrder = CallLog.Calls.DATE + " DESC"
        val numberCol = CallLog.Calls.NUMBER
        val durationCol = CallLog.Calls.DURATION
        val typeCol = CallLog.Calls.TYPE // 1 - Incoming, 2 - Outgoing, 3 - Missed

        val projection = arrayOf(numberCol, durationCol, typeCol)

        val cursor = requireActivity().contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null, null, null, sortOrder
        )!!

        while (cursor.moveToNext()) {
            val number = cursor.getString(cursor.getColumnIndex(numberCol))
            var duration = cursor.getString( cursor.getColumnIndex(durationCol))
            var type = cursor.getString(cursor.getColumnIndex(typeCol))
            val srt_call_full_date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE))

            val dateFormatter = SimpleDateFormat("dd MMM yyyy")
            val str_call_date = dateFormatter.format(Date(srt_call_full_date.toLong()))

            val timeFormatter = SimpleDateFormat(
                "HH:mm:ss"
            )
            val srt_call_time = timeFormatter.format(Date(srt_call_full_date.toLong()))

            //str_call_time = getFormatedDateTime(str_call_time, "HH:mm:ss", "hh:mm ss");
            duration = DurationFormat(duration)

            when (type.toInt()) {
                CallLog.Calls.INCOMING_TYPE -> type = "Incoming"
                CallLog.Calls.OUTGOING_TYPE -> type = "Outgoing"
                CallLog.Calls.MISSED_TYPE -> type = "Missed"
                CallLog.Calls.VOICEMAIL_TYPE -> type = "Voicemail"
                CallLog.Calls.REJECTED_TYPE -> type = "Rejected"
                CallLog.Calls.BLOCKED_TYPE -> type = "Blocked"
                CallLog.Calls.ANSWERED_EXTERNALLY_TYPE -> type = "Externally Answered"
                else -> type = "NA"
            }
            Log.d("thong", "$number $duration $type $srt_call_time $str_call_date $type")
        }

        cursor.close()
    }

    private fun DurationFormat(duration: String): String? {
        var durationFormatted: String? = null
        durationFormatted = if (duration.toInt() < 60) {
            "$duration sec"
        } else {
            val min = duration.toInt() / 60
            val sec = duration.toInt() % 60
            if (sec == 0) "$min min" else "$min min $sec sec"
        }
        return durationFormatted
    }
}