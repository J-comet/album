package hs.project.album.dialog


import android.os.Bundle
import android.util.Log
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primecalendar.common.CalendarFactory.newInstance
import com.aminography.primecalendar.common.CalendarType
import com.aminography.primedatepicker.common.BackgroundShapeType
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.SingleDayPickCallback
import com.aminography.primedatepicker.picker.theme.LightThemeFactory
import hs.project.album.R
import hs.project.album.databinding.DialogAddBabyBinding
import hs.project.album.viewmodel.AddBabyVM
import java.text.SimpleDateFormat
import java.util.*


class AddBabyDialog : DialogFragment(), View.OnClickListener {

    private lateinit var binding: DialogAddBabyBinding
    private var spinnerResult = ""
    lateinit var model: AddBabyVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_fullscreen)
        isCancelable = true // 화면밖 or 뒤로가기버튼으로 dialog dismiss = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddBabyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = ViewModelProvider(requireActivity()).get(AddBabyVM::class.java)
        init()
    }


    private fun init() {
        binding.btnBack.setOnClickListener(this)
        binding.clayoutBtnRegister.setOnClickListener(this)
        binding.btnDatePicker.setOnClickListener(this)

        setSpinnerView()
    }

    private fun setSpinnerView() {
        val spinnerItems = requireActivity().resources?.getStringArray(R.array.arr_gender)
        val spinnerAdapter =
            ArrayAdapter(requireActivity(), R.layout.item_spinner_gender, spinnerItems!!)

        binding.spinnerGender.adapter = spinnerAdapter
        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinnerResult = spinnerItems[position].toString()
            }
        }

        // 스피너 아이템안의 색상등등 조절용
//        val spinnerAdapter =
//            object : ArrayAdapter<String>(requireActivity(), R.layout.item_spinner_gender, spinnerItems!!) {
//
//                override fun isEnabled(position: Int): Boolean {
//                    return position != 0
//                }
//
//                override fun getDropDownView(
//                    position: Int,
//                    convertView: View?,
//                    parent: ViewGroup
//                ): View {
//                    val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
//                    if(position == 0) {
//                        view.text = requireActivity().resources.getString(R.string.str_common_11)
//                        view.setTextColor(ContextCompat.getColorStateList(
//                            requireActivity(),
//                            R.color.color_808080
//                        ))
//                    } else {
//                        view.setTextColor(ContextCompat.getColorStateList(
//                            requireActivity(),
//                            R.color.black
//                        ))
//                    }
//                    return view
//                }
//
//            }
    }

    private fun showDatePicker() {
        val themeFactory = object : LightThemeFactory() {

            override val pickedDayBackgroundShapeType: BackgroundShapeType
                get() = BackgroundShapeType.CIRCLE

            override val calendarViewPickedDayBackgroundColor: Int
                get() = ContextCompat.getColor(requireActivity(),R.color.color_4facfe)

            override val calendarViewTodayLabelTextColor: Int
                get() = getColor(R.color.blue500)

//            override val calendarViewWeekLabelFormatter: LabelFormatter
//                get() = { primeCalendar ->
//                    when (primeCalendar[Calendar.DAY_OF_WEEK]) {
//                        Calendar.SATURDAY,
//                        Calendar.SUNDAY -> String.format("%s????", primeCalendar.weekDayNameShort)
//                        else -> String.format("%s", primeCalendar.weekDayNameShort)
//                    }
//                }

            override val calendarViewWeekLabelTextColors: SparseIntArray
                get() = SparseIntArray(7).apply {
                    val red = getColor(R.color.red300)
                    val black = getColor(R.color.black)
                    put(Calendar.SATURDAY, red)
                    put(Calendar.SUNDAY, red)
                    put(Calendar.MONDAY, black)
                    put(Calendar.TUESDAY, black)
                    put(Calendar.WEDNESDAY, black)
                    put(Calendar.THURSDAY, black)
                    put(Calendar.FRIDAY, black)
                }

            override val calendarViewShowAdjacentMonthDays: Boolean
                get() = false

            override val selectionBarBackgroundColor: Int
                get() = getColor(R.color.color_6495ed)
            
        }

        val callback = SingleDayPickCallback { day ->
            val strBirthDay = day.longDateString

            val arr = strBirthDay.split("," , " ")
            println(arr)
            val resultBirthDay =
                arr[4].trim() + "년 " +
                        arr[3].trim() + " " +
                        arr[2].trim() + "일 " +
                        arr[0].trim()

            binding.tvBirthday.text = resultBirthDay
            Log.e("birth", resultBirthDay)

            showDdayText(arr)
        }

        val maxDate = newInstance(CalendarType.CIVIL, Locale.getDefault())
        maxDate.add(Calendar.MONTH, 0)

        val minDate = newInstance(CalendarType.CIVIL, Locale.getDefault())
        minDate.add(Calendar.YEAR, -50)

        val today = CivilCalendar()

        val datePicker = PrimeDatePicker.bottomSheetWith(today)
            .pickSingleDay(callback)
            .initiallyPickedSingleDay(today)
            .minPossibleDate(minDate)
            .maxPossibleDate(maxDate)
            .applyTheme(themeFactory)
            .build()

        datePicker.show(childFragmentManager, "DatePicker")
    }

    private fun showDdayText(arr: List<String>) {
        val year = arr[4].trim()
        var month = arr[3].replace("월","").trim()
        var day = arr[2].trim()

        // 'month' 한글자 일 때
        if (month.length < 2) {
            month = "0$month"
        }

        // 'day' 한글자 일 때
        if (day.length < 2) {
            day = "0$day"
        }

        val strResultDate = year + month + day

        val startDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).parse(strResultDate).time
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time.time

        binding.tvDday.text = "태어난지 ${(today - startDate) / (24 * 60 * 60 * 1000)}일"
    }

//    private fun sendDataThisFragToParentFrag(){
//        val bundle = Bundle()
//        bundle.putString("name", binding.tilName.editText?.text.toString())
//        bundle.putString("selected_gender", spinnerResult)
//        bundle.putString("birthday", binding.tvBirthday.text.toString() )
//        childFragmentManager.setFragmentResult("AddBabyDialogResult", bundle)
//    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnBack.id -> {
                dismiss()
            }

            binding.btnDatePicker.id -> {
                showDatePicker()
            }

            binding.clayoutBtnRegister.id -> {
//                sendDataThisFragToParentFrag()
                model.sendData(
                    binding.tilName.editText?.text.toString(),
                    spinnerResult,
                    binding.tvBirthday.text.toString()
                )
                dismiss()
            }
        }
    }


}
