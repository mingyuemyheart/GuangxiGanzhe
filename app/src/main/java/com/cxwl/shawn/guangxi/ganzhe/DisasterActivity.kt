package com.cxwl.shawn.guangxi.ganzhe;

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.AbsListView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import com.cxwl.shawn.guangxi.ganzhe.adapter.DisasterAdapter
import com.cxwl.shawn.guangxi.ganzhe.common.CONST
import com.cxwl.shawn.guangxi.ganzhe.dto.DisasterDto
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil
import kotlinx.android.synthetic.main.activity_disaster.*
import kotlinx.android.synthetic.main.layout_date.*
import kotlinx.android.synthetic.main.shawn_layout_title.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import wheelview.NumericWheelAdapter
import wheelview.OnWheelScrollListener
import wheelview.WheelView
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 灾情列表
 */
class DisasterActivity : ShawnBaseActivity(), OnClickListener {

	private var page = 1
	private var mAdapter: DisasterAdapter? = null
	private val dataList: MutableList<DisasterDto> = ArrayList()
	private var isStart = true
	private val sdf1 = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
	private var check = 1001//1001单日、1002时间段、1003关键词

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_disaster)
		initRefreshLayout()
		initWidget()
		initListView()
		initWheelView()
	}

	/**
	 * 初始化下拉刷新布局
	 */
	private fun initRefreshLayout() {
		refreshLayout.setColorSchemeResources(CONST.color1, CONST.color2, CONST.color3, CONST.color4)
		refreshLayout.setProgressViewEndTarget(true, 400)
		refreshLayout.isRefreshing = true
		refreshLayout.setOnRefreshListener { refresh() }
	}

	private fun refresh() {
		dataList.clear()
		page = 1
		okHttpList()
	}

	private fun initWidget() {
		llBack.setOnClickListener(this)
		tvSearch.setOnClickListener(this)
		tvStartTime.setOnClickListener(this)
		tvEndTime.setOnClickListener(this)
		tvNegtive.setOnClickListener(this)
		tvPositive.setOnClickListener(this)
		tv1.setOnClickListener(this)
		tv2.setOnClickListener(this)
		tv3.setOnClickListener(this)
		val title = intent.getStringExtra(CONST.ACTIVITY_NAME)
		if (!TextUtils.isEmpty(title)) {
			tvTitle.text = title
		}
		refresh()
	}

	private fun initListView() {
		mAdapter = DisasterAdapter(this, dataList)
		listView.adapter = mAdapter
		listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
			val data = dataList[position]
			val intent = Intent(this@DisasterActivity, DisasterDetailActivity::class.java)
			val bundle = Bundle()
			bundle.putParcelable("data", data)
			intent.putExtras(bundle)
			startActivityForResult(intent, 1001)
		}
		listView.setOnScrollListener(object : AbsListView.OnScrollListener {
			override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && view.lastVisiblePosition == view.count - 1) {
					page++
					okHttpList()
				}
			}
			override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}
		})
	}

	private fun okHttpList() {
		refreshLayout!!.visibility = View.VISIBLE
		val url = "http://decision-admin.tianqi.cn/home/work2019/decisionZqfkSelect"
		val builder = FormBody.Builder()

		when (check) {
			1001 -> {//单日
				if (!TextUtils.equals(tvStartTime.text, "选择时间")) {
					builder.add("sel_time", tvStartTime.text.toString())
				}
			}
			1002 -> {//时间段
				if (!TextUtils.equals(tvStartTime.text, "选择时间") && !TextUtils.equals(tvEndTime.text, "选择时间")) {
					try {
						val start = sdf1.parse(tvStartTime.text.toString()).time
						val end = sdf1.parse(tvEndTime.text.toString()).time
						if (start > end) {
							Toast.makeText(this@DisasterActivity, "开始时间不能大于结束时间", Toast.LENGTH_SHORT).show()
							return
						}
					} catch (e: ParseException) {
						e.printStackTrace()
					}
					builder.add("st", tvStartTime.text.toString())
					builder.add("et", tvEndTime.text.toString())
				}
			}
			1003 -> {//关键词
				if (!TextUtils.equals(tvStartTime.text, "选择时间") && !TextUtils.equals(tvEndTime.text, "选择时间")) {
					try {
						val start = sdf1.parse(tvStartTime.text.toString()).time
						val end = sdf1.parse(tvEndTime.text.toString()).time
						if (start > end) {
							Toast.makeText(this@DisasterActivity, "开始时间不能大于结束时间", Toast.LENGTH_SHORT).show()
							return
						}
					} catch (e: ParseException) {
						e.printStackTrace()
					}
					builder.add("st", tvStartTime.text.toString())
					builder.add("et", tvEndTime.text.toString())
				}
				builder.add("key", etSearch!!.text.toString())
			}
		}
		builder.add("page", page.toString() + "")
		val body: RequestBody = builder.build()
		Thread(Runnable {
			OkHttpUtil.enqueue(Request.Builder().post(body).url(url).build(), object : Callback {
				override fun onFailure(call: Call, e: IOException) {}

				@Throws(IOException::class)
				override fun onResponse(call: Call, response: Response) {
					if (!response.isSuccessful) {
						return
					}
					val result = response.body!!.string()
					runOnUiThread {
						refreshLayout!!.isRefreshing = false
						if (!TextUtils.isEmpty(result)) {
							try {
								val obj = JSONObject(result)
								if (!obj.isNull("data")) {
									val array = obj.getJSONArray("data")
									for (i in 0 until array.length()) {
										val itemObj = array.getJSONObject(i)
										val dto = DisasterDto()
										if (!itemObj.isNull("id")) {
											dto.id = itemObj.getString("id")
										}
										if (!itemObj.isNull("username")) {
											dto.userName = itemObj.getString("username")
										}
										if (!itemObj.isNull("mobile")) {
											dto.mobile = itemObj.getString("mobile")
										}
										if (!itemObj.isNull("u_name")) {
											dto.uName = itemObj.getString("u_name")
										}
										if (!itemObj.isNull("title")) {
											dto.title = itemObj.getString("title")
										}
										if (!itemObj.isNull("type")) {
											dto.disasterType = itemObj.getString("type")
										}
										if (!itemObj.isNull("location")) {
											dto.addr = itemObj.getString("location")
										}
										if (!itemObj.isNull("addtime")) {
											dto.time = itemObj.getString("addtime")
										}
										if (!itemObj.isNull("other_param2")) {
											dto.gzType = itemObj.getString("other_param2")
										}
										if (!itemObj.isNull("other_param3")) {
											dto.gzTime = itemObj.getString("other_param3")
										}
										if (!itemObj.isNull("other_param1")) {
											dto.miao = itemObj.getString("other_param1")
										}
										if (!itemObj.isNull("latlon")) {
											dto.latlon = itemObj.getString("latlon")
										}
										if (!itemObj.isNull("content")) {
											dto.content = itemObj.getString("content")
										}
										if (!itemObj.isNull("pic")) {
											val imgArray = itemObj.getJSONArray("pic")
											for (j in 0 until imgArray.length()) {
												dto.imgList.add(imgArray.getString(j))
											}
										}
										dataList.add(dto)
									}
									if (mAdapter != null) {
										mAdapter!!.notifyDataSetChanged()
									}
								}
							} catch (e: JSONException) {
								e.printStackTrace()
							}
						}
					}
				}
			})
		}).start()
	}

	override fun onClick(v: View) {
		when (v.id) {
			R.id.llBack -> finish()
			R.id.tv1 -> {
				check = 1001
				tv1.setTextColor(Color.WHITE)
				tv2.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
				tv3.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
				tv1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
				tv2.setBackgroundColor(Color.WHITE)
				tv3.setBackgroundColor(Color.WHITE)
				tvStart.text = "选择时间："
				tvStart.visibility = View.VISIBLE
				tvStartTime.visibility = View.VISIBLE
				tvEnd.visibility = View.GONE
				tvEndTime.visibility = View.GONE
				ivSearch.visibility = View.GONE
				etSearch.visibility = View.GONE
			}
			R.id.tv2 -> {
				check = 1002
				tv1.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
				tv2.setTextColor(Color.WHITE)
				tv3.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
				tv1.setBackgroundColor(Color.WHITE)
				tv2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
				tv3.setBackgroundColor(Color.WHITE)
				tvStart.text = "开始时间："
				tvStart.visibility = View.VISIBLE
				tvEnd.text = "结束时间："
				tvEnd.visibility = View.VISIBLE
				tvEndTime.visibility = View.VISIBLE
				ivSearch.visibility = View.GONE
				etSearch.visibility = View.GONE
			}
			R.id.tv3 -> {
				check = 1003
				tv1.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
				tv2.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
				tv3.setTextColor(Color.WHITE)
				tv1.setBackgroundColor(Color.WHITE)
				tv2.setBackgroundColor(Color.WHITE)
				tv3.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
				tvStart.text = "开始时间："
				tvStart.visibility = View.VISIBLE
				tvEnd.text = "结束时间："
				tvEnd.visibility = View.VISIBLE
				tvEndTime.visibility = View.VISIBLE
				ivSearch.visibility = View.VISIBLE
				etSearch.visibility = View.VISIBLE
			}
			R.id.tvStartTime -> {
				isStart = true
				bootTimeLayoutAnimation()
			}
			R.id.tvNegtive -> bootTimeLayoutAnimation()
			R.id.tvPositive -> {
				bootTimeLayoutAnimation()
				if (isStart) {
					tvStartTime.text = setTextViewValue()
				} else {
					tvEndTime.text = setTextViewValue()
				}
			}
			R.id.tvEndTime -> {
				isStart = false
				bootTimeLayoutAnimation()
			}
			R.id.tvSearch -> {
				dataList.clear()
				okHttpList()
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (resultCode == Activity.RESULT_OK) {
			when(requestCode) {
				1001 -> refresh()
			}
		}
	}

	private fun initWheelView() {
		val c = Calendar.getInstance()
		val curYear = c[Calendar.YEAR]
		val curMonth = c[Calendar.MONTH] + 1 //通过Calendar算出的月数要+1
		val curDate = c[Calendar.DATE]
		val numericWheelAdapter1 = NumericWheelAdapter(this, 1950, curYear)
		numericWheelAdapter1.setLabel("年")
		year.viewAdapter = numericWheelAdapter1
		year.isCyclic = false //是否可循环滑动
		year.addScrollingListener(scrollListener)
		year.visibleItems = 7
		val numericWheelAdapter2 = NumericWheelAdapter(this, 1, 12, "%02d")
		numericWheelAdapter2.setLabel("月")
		month.viewAdapter = numericWheelAdapter2
		month.isCyclic = false
		month.addScrollingListener(scrollListener)
		month.visibleItems = 7
		initDay(curYear, curMonth)
		day.isCyclic = false
		day.visibleItems = 7
		year.currentItem = curYear - 1950
		month.currentItem = curMonth - 1
		day.currentItem = curDate - 1
	}

	private val scrollListener: OnWheelScrollListener = object : OnWheelScrollListener {
		override fun onScrollingStarted(wheel: WheelView) {}
		override fun onScrollingFinished(wheel: WheelView) {
			val nYear = year!!.currentItem + 1950 //年
			val nMonth: Int = month.currentItem + 1 //月
			initDay(nYear, nMonth)
		}
	}

	/**
	 */
	private fun initDay(arg1: Int, arg2: Int) {
		val numericWheelAdapter = NumericWheelAdapter(this, 1, getDay(arg1, arg2), "%02d")
		numericWheelAdapter.setLabel("日")
		day.viewAdapter = numericWheelAdapter
	}

	/**
	 *
	 * @param year
	 * @param month
	 * @return
	 */
	private fun getDay(year: Int, month: Int): Int {
		var day = 30
		var flag = false
		flag = when (year % 4) {
			0 -> true
			else -> false
		}
		day = when (month) {
			1, 3, 5, 7, 8, 10, 12 -> 31
			2 -> if (flag) 29 else 28
			else -> 30
		}
		return day
	}

	/**
	 */
	private fun setTextViewValue(): String? {
		val yearStr = (year!!.currentItem + 1950).toString()
		val monthStr = if (month.currentItem + 1 < 10) "0" + (month.currentItem + 1) else (month.currentItem + 1).toString()
		val dayStr = if (day.currentItem + 1 < 10) "0" + (day.currentItem + 1) else (day.currentItem + 1).toString()
		return "$yearStr-$monthStr-$dayStr"
	}

	private fun bootTimeLayoutAnimation() {
		if (layoutDate!!.visibility == View.GONE) {
			timeLayoutAnimation(true, layoutDate)
			layoutDate!!.visibility = View.VISIBLE
		} else {
			timeLayoutAnimation(false, layoutDate)
			layoutDate!!.visibility = View.GONE
		}
	}

	/**
	 * 时间图层动画
	 * @param flag
	 * @param view
	 */
	private fun timeLayoutAnimation(flag: Boolean, view: View?) {
		//列表动画
		val animationSet = AnimationSet(true)
		val animation: TranslateAnimation = if (!flag) {
			TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0f,
					Animation.RELATIVE_TO_SELF, 0f,
					Animation.RELATIVE_TO_SELF, 0f,
					Animation.RELATIVE_TO_SELF, 1f)
		} else {
			TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0f,
					Animation.RELATIVE_TO_SELF, 0f,
					Animation.RELATIVE_TO_SELF, 1f,
					Animation.RELATIVE_TO_SELF, 0f)
		}
		animation.duration = 400
		animationSet.addAnimation(animation)
		animationSet.fillAfter = true
		view!!.startAnimation(animationSet)
		animationSet.setAnimationListener(object : Animation.AnimationListener {
			override fun onAnimationStart(arg0: Animation) {}
			override fun onAnimationRepeat(arg0: Animation) {}
			override fun onAnimationEnd(arg0: Animation) {
				view.clearAnimation()
			}
		})
	}

}
