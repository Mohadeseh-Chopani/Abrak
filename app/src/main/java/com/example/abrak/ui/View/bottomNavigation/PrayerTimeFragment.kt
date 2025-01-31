package com.example.abrak.ui.View.bottomNavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.example.abrak.R
import com.example.abrak.data.models.PrayerTimeData
import com.example.abrak.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale



class PrayerTimeFragment(val prayerTimeData: MutableLiveData<PrayerTimeData>) : BottomSheetDialogFragment() {

    var textDhuhr: TextView? = null
    private lateinit var binding: FragmentBottomSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBinding.inflate(layoutInflater)


        prayerTimeData.value?.result.let { result ->
            binding.dataShamsi.text = "1403"
            binding.monthShamsi.text = result?.month
            binding.dayShamsi.text = result?.day
            binding.fajrTime.text =  result?.azan_sobh
            textDhuhr?.text = result?.azan_zohre.toString()
            binding.dhuhrTime.text =  result?.azan_zohre
            binding.maghribTime.text =  result?.azan_maghreb
            binding.sunsetTime.text =  result?.ghorob_aftab
            binding.sunriseTime.text =  result?.toloe_aftab
            binding.midnightTime.text =  result?.nime_shabe_sharie
            binding.dataMiladi.text =  miladiData(0)
            binding.monthMiladi.text =  miladiData(1)
            binding.dayMiladi.text =  miladiData(2)
        } ?: run{

        }

        binding.actionButton.setOnClickListener {
            dismiss() // Dismiss the bottom sheet
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textDhuhr = view.findViewById(R.id.dhuhr_time)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    private fun miladiData(index: Int): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = LocalDate.now().format(formatter)
        val liveDate = formattedDate.split("-")
        return liveDate[index]
    }
}
