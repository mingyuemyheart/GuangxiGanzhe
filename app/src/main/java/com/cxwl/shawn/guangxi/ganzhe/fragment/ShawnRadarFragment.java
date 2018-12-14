package com.cxwl.shawn.guangxi.ganzhe.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cxwl.shawn.guangxi.ganzhe.R;
import com.cxwl.shawn.guangxi.ganzhe.adapter.ShawnRadarAdapter;
import com.cxwl.shawn.guangxi.ganzhe.common.CONST;
import com.cxwl.shawn.guangxi.ganzhe.dto.RadarDto;
import com.cxwl.shawn.guangxi.ganzhe.manager.RadarManager;
import com.cxwl.shawn.guangxi.ganzhe.util.OkHttpUtil;
import com.cxwl.shawn.guangxi.ganzhe.util.SecretUrlUtil;
import com.cxwl.shawn.guangxi.ganzhe.view.stickygridheaders.StickyGridHeadersGridView;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 天气雷达
 */
public class ShawnRadarFragment extends Fragment implements View.OnClickListener {

    private ImageView imageView;
    private LinearLayout llSeekBar;
    private ImageView ivPlay;
    private SeekBar seekBar;
    private TextView tvTime;
    private List<RadarDto> radarList = new ArrayList<>();
    private RadarManager radarManager;
    private RadarThread radarThread;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.CHINA);
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private ShawnRadarAdapter mAdapter;
    private List<RadarDto> dataList = new ArrayList<>();
    private int section = 1;
    private HashMap<String, Integer> sectionMap = new HashMap<>();
    private AVLoadingIndicatorView loadingView;
    private ScrollView scrollView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shawn_fragment_radar, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget(view);
        initGridView(view);
    }

    /**
     * 初始化控件
     */
    private void initWidget(View view) {
        loadingView = view.findViewById(R.id.loadingView);
        imageView = view.findViewById(R.id.imageView);
        imageView.setOnClickListener(this);
        imageView.setFocusable(true);
        imageView.setFocusableInTouchMode(true);
        imageView.requestFocus();
        ivPlay = view.findViewById(R.id.ivPlay);
        ivPlay.setOnClickListener(this);
        seekBar = view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(seekbarListener);
        tvTime = view.findViewById(R.id.tvTime);
        llSeekBar = view.findViewById(R.id.llSeekBar);
        scrollView = view.findViewById(R.id.scrollView);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = width;
        params.height = width*3/4;
        imageView.setLayoutParams(params);

        radarManager = new RadarManager(getActivity());
    }

    private SeekBar.OnSeekBarChangeListener seekbarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar arg0) {
            if (radarThread != null) {
                radarThread.setCurrent(seekBar.getProgress());
                radarThread.stopTracking();
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
            if (radarThread != null) {
                radarThread.startTracking();
            }
        }
        @Override
        public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        }
    };

    /**
     * 初始化gridview
     */
    private void initGridView(View view) {
        dataList.clear();
        String radarCode = "JC_RADAR_AZ9771_JB";//默认为南宁雷达站
        String[] stations = getResources().getStringArray(R.array.radars_station);
        for (int i = 0; i < stations.length; i++) {
            String[] value = stations[i].split(",");
            RadarDto dto = new RadarDto();
            dto.sectionName = value[0];
            dto.radarName = value[1];
            dto.radarCode = value[2];
            dto.adcode = value[4];
            if (CONST.ADCODE.startsWith(dto.adcode)) {
                dto.isSelected = true;
                radarCode = dto.radarCode;
            }else {
                dto.isSelected = false;
            }
            dataList.add(dto);
        }

        for (int i = 0; i < dataList.size(); i++) {
            RadarDto sectionDto = dataList.get(i);
            if (!sectionMap.containsKey(sectionDto.sectionName)) {
                sectionDto.section = section;
                sectionMap.put(sectionDto.sectionName, section);
                section++;
            }else {
                sectionDto.section = sectionMap.get(sectionDto.sectionName);
            }
            dataList.set(i, sectionDto);
        }

        StickyGridHeadersGridView gridView = view.findViewById(R.id.gridView);
        mAdapter = new ShawnRadarAdapter(getActivity(), dataList);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                for (int i = 0; i < dataList.size(); i++) {
                    RadarDto dto = dataList.get(i);
                    if (i == arg2) {
                        dto.isSelected = true;
                    }else {
                        dto.isSelected = false;
                    }
                }
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }

                RadarDto dto = dataList.get(arg2);
                OkHttpRadarInfo(dto.radarCode);
            }
        });

        OkHttpRadarInfo(radarCode);
    }

    /**
     * 获取雷达图片集信息
     */
    private void OkHttpRadarInfo(String radarCode) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        loadingView.setVisibility(View.VISIBLE);
        final String url = SecretUrlUtil.weatherRadar(radarCode);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String result = response.body().string();
                        if (!isAdded()) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        radarList.clear();
                                        String r2 = obj.getString("r2");
                                        String r3 = obj.getString("r3");
                                        String[] temp = obj.getString("r4").split("\\|");
                                        String r4 = temp[0];
                                        String r5 = obj.getString("r5");
                                        JSONArray array = new JSONArray(obj.getString("r6"));
                                        for (int i = array.length()-1; i >= 0 ; i--) {
                                            JSONArray itemArray = array.getJSONArray(i);
                                            String r6_0 = itemArray.getString(0);
                                            String r6_1 = itemArray.getString(1);
                                            String url = r2 + r4 + "/" + r5 + r6_0 + "." + r3;

                                            if (i == 0 && !TextUtils.isEmpty(url)) {
                                                Picasso.get().load(url).into(imageView);
                                                try {
                                                    tvTime.setText(sdf1.format(sdf2.parse(r6_1)));
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            RadarDto dto = new RadarDto();
                                            if (i == 0) {
                                                dto.isSelected = true;
                                            }else {
                                                dto.isSelected = false;
                                            }
                                            dto.imgUrl = url;
                                            dto.time0 = r6_0;
                                            dto.time = r6_1;
                                            radarList.add(dto);
                                        }
                                        llSeekBar.setVisibility(View.VISIBLE);
                                        startDownLoadImgs();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                loadingView.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private void startDownLoadImgs() {
        loadingView.setVisibility(View.VISIBLE);
        if (radarThread != null) {
            radarThread.cancel();
            radarThread = null;
        }
        if (radarList.size() <= 0) {
            loadingView.setVisibility(View.GONE);
            return;
        }
        radarManager.loadImagesAsyn(radarList, new RadarManager.RadarListener() {
            @Override
            public void onResult(int result, List<RadarDto> images) {
                if (result == RadarManager.RadarListener.RESULT_SUCCESSED) {
                    if (images.size() > 0) {
                        radarThread = new RadarThread(images);
                    }
                }
            }
            @Override
            public void onProgress(String url, int progress) {
            }
        });
    }

    private class RadarThread extends Thread {
        static final int STATE_NONE = 0;
        static final int STATE_PLAYING = 1;
        static final int STATE_PAUSE = 2;
        static final int STATE_CANCEL = 3;
        private List<RadarDto> images;
        private int state;
        private int index;
        private int count;
        private boolean isTracking;

        private RadarThread(List<RadarDto> images) {
            this.images = images;
            this.count = images.size();
            this.index = 0;
            this.state = STATE_NONE;
            this.isTracking = false;
        }

        private int getCurrentState() {
            return state;
        }

        @Override
        public void run() {
            super.run();
            this.state = STATE_PLAYING;
            while (true) {
                if (state == STATE_CANCEL) {
                    break;
                }
                if (state == STATE_PAUSE) {
                    continue;
                }
                if (isTracking) {
                    continue;
                }
                sendRadar();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendRadar() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (index >= count || index < 0) {
                        index = 0;
                    }else {
                        RadarDto radar = images.get(index);
                        Bitmap bitmap = BitmapFactory.decodeFile(radar.imgPath);
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                        changeProgress(radar.time, index++, count-1);
                    }
                }
            });
        }

        private void cancel() {
            this.state = STATE_CANCEL;
        }
        private void pause() {
            this.state = STATE_PAUSE;
        }
        private void play() {
            this.state = STATE_PLAYING;
        }

        private void setCurrent(int index) {
            this.index = index;
        }

        private void startTracking() {
            isTracking = true;
        }

        private void stopTracking() {
            isTracking = false;
            if (this.state == STATE_PAUSE) {
                sendRadar();
            }
        }
    }

    private void changeProgress(String time, int progress, int max) {
        if (seekBar != null) {
            seekBar.setMax(max);
            seekBar.setProgress(progress);
        }
        try {
            tvTime.setText(sdf1.format(sdf2.parse(time)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivPlay:
                if (radarThread != null) {
                    if (radarThread.getCurrentState() == RadarThread.STATE_NONE) {
                        radarThread.start();
                        ivPlay.setImageResource(R.drawable.shawn_icon_pause);
                    }else if (radarThread.getCurrentState() == RadarThread.STATE_PLAYING) {
                        radarThread.pause();
                        ivPlay.setImageResource(R.drawable.shawn_icon_play);
                    }else if (radarThread.getCurrentState() == RadarThread.STATE_PAUSE) {
                        radarThread.play();
                        ivPlay.setImageResource(R.drawable.shawn_icon_pause);
                    }
                }
                break;
            case R.id.imageView:
//                for (RadarDto dto : radarList) {
//                    if (dto.isSelected) {
//                        if (radarThread != null && radarThread.getCurrentState() == RadarThread.STATE_PLAYING) {
//                            radarThread.pause();
//                            ivPlay.setImageResource(R.drawable.shawn_icon_play);
//                        }
//
////                        Intent intent = new Intent(mContext, ImageZoomActivity.class);
////                        intent.putExtra(CONST.WEB_URL, radarList.get(i).url);
////                        startActivity(intent);
//                        break;
//                    }
//                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (radarManager != null) {
            radarManager.onDestory();
        }
        if (radarThread != null) {
            radarThread.cancel();
            radarThread = null;
        }
    }

}
