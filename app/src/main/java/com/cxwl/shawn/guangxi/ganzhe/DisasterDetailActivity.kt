package com.cxwl.shawn.guangxi.ganzhe

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.cxwl.shawn.guangxi.ganzhe.common.CONST
import com.cxwl.shawn.guangxi.ganzhe.common.MyApplication
import com.cxwl.shawn.guangxi.ganzhe.dto.DisasterDto
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_disaster_detail.*
import kotlinx.android.synthetic.main.shawn_dialog_cache.view.*
import kotlinx.android.synthetic.main.shawn_layout_title.*
import okhttp3.*
import org.json.JSONObject
import uk.co.senab.photoview.PhotoView
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener
import java.io.IOException

/**
 * 我上传的灾情反馈-详情
 */
class DisasterDetailActivity : ShawnBaseActivity(), OnClickListener {

	var data: DisasterDto? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_disaster_detail)
		initWidget()
	}

	private fun initWidget() {
		llBack.setOnClickListener(this)
		tvTitle.text = "灾情详情"
		tvDelete.setOnClickListener(this)
		tvEdit.setOnClickListener(this)

		data = intent.getParcelableExtra("data")

		if (TextUtils.equals(MyApplication.USERGROUP, "90")) {
			tvDelete.visibility = View.VISIBLE
			tvEdit.visibility = View.VISIBLE
		} else {
			tvDelete.visibility = View.GONE
			tvEdit.visibility = View.GONE
		}

		okHttpDetail()
	}

	/**
	 * 后去灾情详情
	 */
	private fun okHttpDetail() {
		Thread(Runnable {
			val url = "https://decision-admin.tianqi.cn/Home/work2019/gxgz_Zqfksearch"
			val builder = FormBody.Builder()
			builder.add("id", data!!.id)
			val body = builder.build()
			OkHttpUtil.enqueue(Request.Builder().url(url).post(body).build(), object : Callback {
				override fun onFailure(call: Call, e: IOException) {
				}
				override fun onResponse(call: Call, response: Response) {
					if (!response.isSuccessful) {
						return
					}
					val result = response.body!!.string()
					runOnUiThread {
						if (!TextUtils.isEmpty(result)) {
							val obj = JSONObject(result)
							if (!obj.isNull("data")) {
								val itemObj = obj.getJSONObject("data")
								if (!itemObj.isNull("id")) {
									data!!.id = itemObj.getString("id")
								}
								if (!itemObj.isNull("username")) {
									data!!.userName = itemObj.getString("username")
									tvUserName.text = "用户名："+data!!.userName
								}
								if (!itemObj.isNull("mobile")) {
									data!!.mobile = itemObj.getString("mobile")
									tvPhone.text = data!!.mobile
								}
								if (!itemObj.isNull("u_name")) {
									data!!.uName = itemObj.getString("u_name")
									tvFullName.text = "姓名："+data!!.uName
								}
								if (!itemObj.isNull("title")) {
									data!!.title = itemObj.getString("title")
									tvName.text = data!!.title
								}
								if (!itemObj.isNull("type")) {
									data!!.gzType = itemObj.getString("type")
									tvType.text = data!!.gzType
								}
								if (!itemObj.isNull("location")) {
									data!!.addr = itemObj.getString("location")
									tvAddr.text = "事件地点："+data!!.addr
								}
								if (!itemObj.isNull("addtime")) {
									data!!.time = itemObj.getString("addtime")
									tvTime.text = "事件时间："+data!!.time
								}

								var latLon: String? = null
								if (!itemObj.isNull("latlon")) {
									data!!.latlon = itemObj.getString("latlon")
									if (data!!.latlon.contains(",")) {
										val latLngs = data!!.latlon.split(",")
										latLon = latLngs[0]+"N,${latLngs[1]}E"
									}
								}
								if (!itemObj.isNull("other_param2")) {
									data!!.gzType = itemObj.getString("other_param2")
								}
								if (!itemObj.isNull("other_param3")) {
									data!!.gzTime = itemObj.getString("other_param3")
								}
								if (!itemObj.isNull("other_param1")) {
									data!!.miao = itemObj.getString("other_param1")
								}
								if (!itemObj.isNull("content")) {
									data!!.content = itemObj.getString("content")
								}
								tvContent.text = "甘蔗品种：${data!!.gzType}\n种植时间：${data!!.gzTime}\n苗情：${data!!.miao}\n经纬度：$latLon\n信息备注：${data!!.content}"

								if (!itemObj.isNull("pic")) {
									val array = itemObj.getJSONArray("pic")
									val size = array.length()
									if (size > 0) {
										data!!.imgList.clear()
										for (i in 0 until size) {
											val imgUrl = array[i].toString()
											data!!.imgList.add(imgUrl)
											val imageView = ImageView(this@DisasterDetailActivity)
											imageView.tag = imgUrl
											imageView.scaleType = ImageView.ScaleType.CENTER_CROP
											val w = (CommonUtil.widthPixels(this@DisasterDetailActivity)-CommonUtil.dip2px(this@DisasterDetailActivity, 20f).toInt()) / 3
											val params = LinearLayout.LayoutParams(w, w)
											params.marginStart = CommonUtil.dip2px(this@DisasterDetailActivity, 5f).toInt()
											imageView.layoutParams = params
											Picasso.get().load(imgUrl).into(imageView)
											llContainer.addView(imageView)
											imageView.setOnClickListener { v ->
												val tag = v.tag.toString() + ""
												for (j in 0 until llContainer.childCount) {
													val iv = llContainer.getChildAt(j) as ImageView
													if (TextUtils.equals(iv.tag.toString() + "", tag)) {
														initViewPager(j, data!!.imgList)
														if (viewPager!!.visibility == View.GONE) {
															scaleExpandAnimation(viewPager)
															viewPager!!.visibility = View.VISIBLE
															tvCount!!.visibility = View.VISIBLE
															tvCount.text = (j + 1).toString() + "/" + size
														}
														break
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			})
		}).start()
	}

	private fun deleteDisaster() {
		val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		val view = inflater.inflate(R.layout.shawn_dialog_cache, null)
		view.tvContent.text = "确定要删除该条灾情吗？"
		val dialog = Dialog(this, R.style.CustomProgressDialog)
		dialog.setContentView(view)
		dialog.setCancelable(false)
		dialog.show()
		view.tvNegtive.setOnClickListener {
			dialog.dismiss()
		}
		view.tvPositive.setOnClickListener {
			dialog.dismiss()
			okHttpDelete()
		}
	}

	/**
	 * 删除灾情
	 */
	private fun okHttpDelete() {
		Thread(Runnable {
			val url = "http://decision-admin.tianqi.cn/Home/work2019/gxgz_Zqfkremove"
			val builder = FormBody.Builder()
			builder.add("id", data!!.id)
			val body = builder.build()
			OkHttpUtil.enqueue(Request.Builder().url(url).post(body).build(), object : Callback {
				override fun onFailure(call: Call, e: IOException) {
				}
				override fun onResponse(call: Call, response: Response) {
					if (!response.isSuccessful) {
						return
					}
					val result = response.body!!.string()
					runOnUiThread {
						if (!TextUtils.isEmpty(result)) {
							val obj = JSONObject(result)
							if (!obj.isNull("info")) {
								Toast.makeText(this@DisasterDetailActivity, obj.getString("info"), Toast.LENGTH_SHORT).show()
							}
							if (TextUtils.equals(obj.getString("code"), "200")) {
								setResult(Activity.RESULT_OK)
								finish()
							}
						}
					}
				}
			})
		}).start()
	}

	override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
		if (viewPager!!.visibility == View.VISIBLE) {
			scaleColloseAnimation(viewPager)
			viewPager!!.visibility = View.GONE
			tvCount!!.visibility = View.GONE
			return false
		} else {
			finish()
		}
		return super.onKeyDown(keyCode, event)
	}

	override fun onClick(v: View) {
		when (v.id) {
			R.id.llBack -> finish()
			R.id.tvDelete -> deleteDisaster()
			R.id.tvEdit -> {
				val intent = Intent(this, DisasterUploadActivity::class.java)
				intent.putExtra(CONST.ACTIVITY_NAME, "灾情信息修改")
				val bundle = Bundle()
				bundle.putParcelable("data", data)
				intent.putExtras(bundle)
				startActivityForResult(intent, 1001)
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (resultCode == Activity.RESULT_OK) {
			when(requestCode) {
				1001 -> {

				}
			}
		}
	}

	/**
	 * 初始化viewPager
	 */
	private fun initViewPager(current: Int, list: List<String>) {
		val imageArray = arrayOfNulls<ImageView>(list.size)
		for (i in list.indices) {
			val imgUrl = list[i]
			if (!TextUtils.isEmpty(imgUrl)) {
				val imageView = ImageView(this)
				imageView.scaleType = ImageView.ScaleType.CENTER_CROP
				Picasso.get().load(imgUrl).into(imageView)
				imageArray[i] = imageView
			}
		}
		val myViewPagerAdapter = MyViewPagerAdapter(imageArray)
		viewPager.adapter = myViewPagerAdapter
		viewPager.currentItem = current
		viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
			override fun onPageSelected(arg0: Int) {
				tvCount!!.text = (arg0 + 1).toString() + "/" + list.size
			}
			override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
			override fun onPageScrollStateChanged(arg0: Int) {}
		})
	}

	private inner class MyViewPagerAdapter(private val mImageViews: Array<ImageView?>) : PagerAdapter() {
		override fun getCount(): Int {
			return mImageViews.size
		}

		override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
			return arg0 === arg1
		}

		override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
			container.removeView(mImageViews[position])
		}

		override fun instantiateItem(container: ViewGroup, position: Int): Any {
			val photoView = PhotoView(container.context)
			val drawable = mImageViews[position]!!.drawable
			photoView.setImageDrawable(drawable)
			container.addView(photoView, 0)
			photoView.onPhotoTapListener = OnPhotoTapListener { view, v, v1 ->
				scaleColloseAnimation(viewPager)
				viewPager.visibility = View.GONE
				tvCount.visibility = View.GONE
			}
			return photoView
		}

	}

	/**
	 * 放大动画
	 * @param view
	 */
	private fun scaleExpandAnimation(view: View?) {
		val animationSet = AnimationSet(true)
		val scaleAnimation = ScaleAnimation(0f, 1.0f, 0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
		scaleAnimation.interpolator = LinearInterpolator()
		scaleAnimation.duration = 300
		animationSet.addAnimation(scaleAnimation)
		val alphaAnimation = AlphaAnimation(0f, 1.0f)
		alphaAnimation.duration = 300
		animationSet.addAnimation(alphaAnimation)
		view!!.startAnimation(animationSet)
	}

	/**
	 * 缩小动画
	 * @param view
	 */
	private fun scaleColloseAnimation(view: View?) {
		val animationSet = AnimationSet(true)
		val scaleAnimation = ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
		scaleAnimation.interpolator = LinearInterpolator()
		scaleAnimation.duration = 300
		animationSet.addAnimation(scaleAnimation)
		val alphaAnimation = AlphaAnimation(1.0f, 0f)
		alphaAnimation.duration = 300
		animationSet.addAnimation(alphaAnimation)
		view!!.startAnimation(animationSet)
	}

}
