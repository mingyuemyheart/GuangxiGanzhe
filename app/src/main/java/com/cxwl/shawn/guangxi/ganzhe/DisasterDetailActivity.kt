package com.cxwl.shawn.guangxi.ganzhe;

import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.*
import android.widget.ImageView
import android.widget.LinearLayout
import com.cxwl.shawn.guangxi.ganzhe.dto.DisasterDto
import com.cxwl.shawn.guangxi.ganzhe.util.CommonUtil
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_disaster_detail.*
import kotlinx.android.synthetic.main.shawn_layout_title.*
import uk.co.senab.photoview.PhotoView
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener

/**
 * 我上传的灾情反馈-详情
 */
class DisasterDetailActivity : ShawnBaseActivity(), OnClickListener {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_disaster_detail)
		initWidget()
	}

	private fun initWidget() {
		llBack.setOnClickListener(this)
		tvTitle.text = "灾情详情"
		val data: DisasterDto = intent.getParcelableExtra("data")
		tvUserName.text = "用户名：${data.userName}"
		tvPhone.text = data.mobile
		tvFullName.text = "姓名：${data.uName}"

		if (!TextUtils.isEmpty(data.title)) {
			tvName.text = data.title
		}
		if (!TextUtils.isEmpty(data.disasterType)) {
			tvType.text = data.disasterType
		}
		if (!TextUtils.isEmpty(data.addr)) {
			tvAddr.text = "事件地点：${data.addr}"
		}
		if (!TextUtils.isEmpty(data.time)) {
			tvTime.text = "事件时间：${data.time}"
		}
		val latLngs = data.latlon.split(",")
		val a = latLngs[0]+"N,${latLngs[1]}E"
		tvContent.text = "甘蔗品种：${data.gzType}\n种植时间：${data.gzTime}\n苗情：${data.miao}\n经纬度：$a\n信息备注：${data.content}"
		if (data.imgList.size > 0) {
			llContainer.removeAllViews()
			val size = data.imgList.size
			for (i in 0 until size) {
				val imgUrl = data.imgList[i]
				val imageView = ImageView(this)
				imageView.tag = imgUrl
				imageView.scaleType = ImageView.ScaleType.CENTER_CROP
				val w = (CommonUtil.widthPixels(this)-CommonUtil.dip2px(this, 20f).toInt()) / 3
				val params = LinearLayout.LayoutParams(w, w)
				params.marginStart = CommonUtil.dip2px(this, 5f).toInt()
				imageView.layoutParams = params
				Picasso.get().load(imgUrl).into(imageView)
				llContainer.addView(imageView)
				imageView.setOnClickListener { v ->
					val tag = v.tag.toString() + ""
					for (j in 0 until llContainer.childCount) {
						val iv = llContainer.getChildAt(j) as ImageView
						if (TextUtils.equals(iv.tag.toString() + "", tag)) {
							initViewPager(j, data.imgList)
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
