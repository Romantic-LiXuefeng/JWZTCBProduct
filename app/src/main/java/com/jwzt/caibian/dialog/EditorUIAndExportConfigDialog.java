package com.jwzt.caibian.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jwzt.caibian.util.SDKUtils;
import com.jwzt.cb.product.R;
import com.rd.veuisdk.manager.UIConfiguration;

/**
 * 视频编辑界面及导出设置对话框
 */
public class EditorUIAndExportConfigDialog {
    private AlertDialog dialog;
    private ConfigData tempData;
    private Activity mActitivy;

    // 对话框界面的控件
    private CheckBox soundTrack, dubbing, filter, titling, specialEffects, subTitleSpecialOuter,
            clipEditing, mv, ckNewApi;
    private CheckBox watermark, textWatermark, videoTrailer;
    private Switch swWizard, swAutoRepeat;
    private CheckBox imageDuration, edit, trim, videoSpeed, split, copy,
            proportion, sort, text, reverse, cbLocalmusic;
    private EditText etExportDuration;
    private EditText etWatermarkXAxis, etWatermarkYAxis, etWatermarkXZoom,
            etWatermarkYZoom, etCustomApi;
    private RadioGroup rgProportion, rgSupportFormat, rgFilter,
            rgVoice;
    private RadioButton rbPorportionSquare, rbPorportionLandscape,
            rbPorportionAuto;
    private View view;

    /**
     * 构造函数
     */
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public EditorUIAndExportConfigDialog(Activity activity,
                                         final ConfigDialogListener listener, ConfigData configData) {
        mActitivy = activity;
        tempData = new ConfigData();
        tempData.setConfig(configData);
        view = activity.getLayoutInflater().inflate(
                R.layout.ui_config_dialog, null);
        initDialogView(view);
        dialog = new AlertDialog.Builder(activity, R.style.dialog).setView(view)
                .setPositiveButton(activity.getString(R.string.back), null)
                .setNegativeButton(activity.getString(R.string.save), new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveUIConfigData();
                        listener.onSaveConfigData(tempData);
                    }
                }).show();
        dialog.setOnDismissListener(listener);
        initFilterState();
    }

    private void initFilterState() {

        filter.setEnabled(!tempData.enableNewApi);
        if (tempData.enableNewApi) {
            filter.setChecked(true);
            //自定义网络素材开启
            view.findViewById(R.id.rbFilter1).setEnabled(false);
            view.findViewById(R.id.rbFilter2).setEnabled(false);
            view.findViewById(R.id.rbFilter3).setEnabled(false);
        } else {
            if (tempData.enableFilter) {
                //允许3选一
                view.findViewById(R.id.rbFilter1).setEnabled(true);
                view.findViewById(R.id.rbFilter2).setEnabled(true);
                view.findViewById(R.id.rbFilter3).setEnabled(true);
            } else {
                //关闭滤镜功能
                view.findViewById(R.id.rbFilter1).setEnabled(false);
                view.findViewById(R.id.rbFilter2).setEnabled(false);
                view.findViewById(R.id.rbFilter3).setEnabled(false);
            }
        }

    }

    private void initDialogView(final View view) {
        etCustomApi = (EditText) view.findViewById(R.id.customApi);
        etCustomApi.setText(tempData.customApi);
        ckNewApi = (CheckBox) view.findViewById(R.id.ckNewApi);
        ckNewApi.setChecked(tempData.enableNewApi);
        ckNewApi.setOnCheckedChangeListener(UICheckListener);
        rbPorportionSquare = (RadioButton) view
                .findViewById(R.id.rbProportionSquare);
        rbPorportionAuto = (RadioButton) view
                .findViewById(R.id.rbProportionAuto);
        rbPorportionLandscape = (RadioButton) view
                .findViewById(R.id.rbProportionLandscape);
        // 控制向导模式开关
        swWizard = (Switch) view.findViewById(R.id.swWizard);
        swWizard.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                TextView tvWizard = (TextView) view.findViewById(R.id.tvWizard);
                if (isChecked) {
                    tvWizard.setText(R.string.openWizard);
                } else {
                    tvWizard.setText(R.string.closeWizard);
                }
                tempData.enableWizard = isChecked;
            }
        });
        swWizard.setChecked(tempData.enableWizard);

        // 控制自动播放开关
        swAutoRepeat = (Switch) view.findViewById(R.id.swAutoRepeat);
        swAutoRepeat.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                TextView tvAutoRepeat = (TextView) view
                        .findViewById(R.id.tvAutoRepeat);
                if (isChecked) {
                    tvAutoRepeat.setText(R.string.openAutoRepeatPlay);
                } else {
                    tvAutoRepeat.setText(R.string.closeAutoRepeatPlay);
                }
                tempData.enableAutoRepeat = isChecked;
            }
        });
        swAutoRepeat.setChecked(tempData.enableAutoRepeat);


        // 配音
        rgVoice = (RadioGroup) view.findViewById(R.id.rgVoiceTrack);
        if (tempData.voiceLayoutType == UIConfiguration.VOICE_LAYOUT_1) {
            rgVoice.check(R.id.rbVoiceTrak1);
        } else if (tempData.voiceLayoutType == UIConfiguration.VOICE_LAYOUT_2) {
            rgVoice.check(R.id.rbVoiceTrak2);
        }
        rgVoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbVoiceTrak1) {
                    tempData.voiceLayoutType = UIConfiguration.VOICE_LAYOUT_1;
                } else if (checkedId == R.id.rbVoiceTrak2) {
                    tempData.voiceLayoutType = UIConfiguration.VOICE_LAYOUT_2;
                    if (!tempData.enableSoundTrack) {
                        rgVoice.check(R.id.rbVoiceTrak1);
                        tempData.voiceLayoutType = UIConfiguration.VOICE_LAYOUT_1;
                        Toast.makeText(mActitivy, mActitivy.getString(R.string.audioToastMsg),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // 滤镜界面选择
        rgFilter = (RadioGroup) view.findViewById(R.id.rgFilter);
        if (tempData.filterLayoutType == UIConfiguration.FILTER_LAYOUT_1) {
            rgFilter.check(R.id.rbFilter1);
        } else if (tempData.filterLayoutType == UIConfiguration.FILTER_LAYOUT_2) {
            rgFilter.check(R.id.rbFilter2);
        } else if (tempData.filterLayoutType == UIConfiguration.FILTER_LAYOUT_3) {
            rgFilter.check(R.id.rbFilter3);
        }
        rgFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbFilter1) {
                    tempData.filterLayoutType = UIConfiguration.FILTER_LAYOUT_1;
                } else if (checkedId == R.id.rbFilter2) {
                    tempData.filterLayoutType = UIConfiguration.FILTER_LAYOUT_2;
                } else if (checkedId == R.id.rbFilter3) {
                    tempData.filterLayoutType = UIConfiguration.FILTER_LAYOUT_3;
                }
            }
        });

        // 选择视频比例
        rgProportion = (RadioGroup) view.findViewById(R.id.rgProportion);
        if (tempData.videoProportionType == UIConfiguration.PROPORTION_AUTO) {
            rgProportion.check(R.id.rbProportionAuto);
        } else if (tempData.videoProportionType == UIConfiguration.PROPORTION_SQUARE) {
            rgProportion.check(R.id.rbProportionSquare);
        } else if (tempData.videoProportionType == UIConfiguration.PROPORTION_LANDSCAPE) {
            rgProportion.check(R.id.rbProportionLandscape);
        }
        rgProportion
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.rbProportionAuto) {
                            tempData.videoProportionType = UIConfiguration.PROPORTION_AUTO;
                        } else if (checkedId == R.id.rbProportionSquare) {
                            tempData.videoProportionType = UIConfiguration.PROPORTION_SQUARE;
                        } else if (checkedId == R.id.rbProportionLandscape) {
                            tempData.videoProportionType = UIConfiguration.PROPORTION_LANDSCAPE;
                        }
                    }
                });

        // 选择秀拍客相册支持的格式
        rgSupportFormat = (RadioGroup) view.findViewById(R.id.rgSupportFormat);
        if (tempData.albumSupportFormatType == UIConfiguration.ALBUM_SUPPORT_DEFAULT) {
            rgSupportFormat.check(R.id.rbSupportDefault);
        } else if (tempData.albumSupportFormatType == UIConfiguration.ALBUM_SUPPORT_IMAGE_ONLY) {
            rgSupportFormat.check(R.id.rbSupportImageOnly);
        } else if (tempData.albumSupportFormatType == UIConfiguration.ALBUM_SUPPORT_VIDEO_ONLY) {
            rgSupportFormat.check(R.id.rbSupportVideoOnly);
        }
        rgSupportFormat
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.rbSupportDefault) {
                            tempData.albumSupportFormatType = UIConfiguration.ALBUM_SUPPORT_DEFAULT;
                        } else if (checkedId == R.id.rbSupportImageOnly) {
                            tempData.albumSupportFormatType = UIConfiguration.ALBUM_SUPPORT_IMAGE_ONLY;
                        } else if (checkedId == R.id.rbSupportVideoOnly) {
                            tempData.albumSupportFormatType = UIConfiguration.ALBUM_SUPPORT_VIDEO_ONLY;
                        }
                    }
                });


        // 设置视频水印位置
        etWatermarkXAxis = (EditText) view.findViewById(R.id.etWatermarkXAxis);
        etWatermarkYAxis = (EditText) view.findViewById(R.id.etWatermarkYAxis);
        etWatermarkXZoom = (EditText) view.findViewById(R.id.etWatermarkXZoom);
        etWatermarkYZoom = (EditText) view.findViewById(R.id.etWatermarkYZoom);
        if (tempData.watermarkShowRectF != null) {
            if (tempData.watermarkShowRectF.left != 0) {
                etWatermarkXAxis.setText("" + tempData.watermarkShowRectF.left);
            }
            if (tempData.watermarkShowRectF.top != 0) {
                etWatermarkYAxis.setText("" + tempData.watermarkShowRectF.top);
            }
            if (tempData.watermarkShowRectF.right != 0) {
                etWatermarkXZoom
                        .setText("" + tempData.watermarkShowRectF.right);
            }
            if (tempData.watermarkShowRectF.bottom != 0) {
                etWatermarkYZoom.setText(""
                        + tempData.watermarkShowRectF.bottom);
            }
        }

        // 限制导出视频时间的EditText
        etExportDuration = (EditText) view.findViewById(R.id.etTimeLimit);
        if (tempData.exportVideoDuration != 0) {
            etExportDuration.setText("" + tempData.exportVideoDuration);
        }


        cbLocalmusic = (CheckBox) view.findViewById(R.id.enableLocalMusic);

        cbLocalmusic.setChecked(tempData.enableLocalMusic);

        // 控制功能模块的CheckBox
        imageDuration = (CheckBox) view.findViewById(R.id.imageDurationControl);
        edit = (CheckBox) view.findViewById(R.id.edit);
        trim = (CheckBox) view.findViewById(R.id.trim);
        videoSpeed = (CheckBox) view.findViewById(R.id.videoSpeedControl);
        split = (CheckBox) view.findViewById(R.id.split);
        copy = (CheckBox) view.findViewById(R.id.copy);
        proportion = (CheckBox) view.findViewById(R.id.proportion);
        sort = (CheckBox) view.findViewById(R.id.sort);
        text = (CheckBox) view.findViewById(R.id.text);
        reverse = (CheckBox) view.findViewById(R.id.reverse);
        mv = (CheckBox) view.findViewById(R.id.mv);

        soundTrack = (CheckBox) view.findViewById(R.id.soundTrack);
        dubbing = (CheckBox) view.findViewById(R.id.VoiceTrack);
        filter = (CheckBox) view.findViewById(R.id.filter);
        titling = (CheckBox) view.findViewById(R.id.titling);
        specialEffects = (CheckBox) view.findViewById(R.id.specialEffects);
        clipEditing = (CheckBox) view.findViewById(R.id.clipEditing);

        watermark = (CheckBox) view.findViewById(R.id.watermark);
        textWatermark = (CheckBox) view.findViewById(R.id.textWatermark);
        videoTrailer = (CheckBox) view.findViewById(R.id.videoTrailer);


        subTitleSpecialOuter = (CheckBox) view.findViewById(R.id.subTitleSpecialEffectOuter);

        subTitleSpecialOuter.setOnCheckedChangeListener(UICheckListener);


        imageDuration.setOnCheckedChangeListener(UICheckListener);
        edit.setOnCheckedChangeListener(UICheckListener);
        trim.setOnCheckedChangeListener(UICheckListener);
        videoSpeed.setOnCheckedChangeListener(UICheckListener);
        split.setOnCheckedChangeListener(UICheckListener);
        copy.setOnCheckedChangeListener(UICheckListener);
        proportion.setOnCheckedChangeListener(UICheckListener);
        sort.setOnCheckedChangeListener(UICheckListener);
        text.setOnCheckedChangeListener(UICheckListener);
        reverse.setOnCheckedChangeListener(UICheckListener);

        soundTrack.setOnCheckedChangeListener(UICheckListener);
        dubbing.setOnCheckedChangeListener(UICheckListener);
        filter.setOnCheckedChangeListener(UICheckListener);
        titling.setOnCheckedChangeListener(UICheckListener);
        specialEffects.setOnCheckedChangeListener(UICheckListener);
        clipEditing.setOnCheckedChangeListener(UICheckListener);
        mv.setOnCheckedChangeListener(UICheckListener);

        watermark.setOnCheckedChangeListener(UICheckListener);
        textWatermark.setOnCheckedChangeListener(UICheckListener);
        videoTrailer.setOnCheckedChangeListener(UICheckListener);
        cbLocalmusic.setOnCheckedChangeListener(UICheckListener);
        imageDuration.setChecked(tempData.enableImageDuration);
        edit.setChecked(tempData.enableEdit);
        trim.setChecked(tempData.enableTrim);
        videoSpeed.setChecked(tempData.enableVideoSpeed);
        split.setChecked(tempData.enableSplit);
        copy.setChecked(tempData.enableCopy);
        proportion.setChecked(tempData.enableProportion);
        sort.setChecked(tempData.enableSort);
        text.setChecked(tempData.enableText);
        reverse.setChecked(tempData.enableReverse);

        soundTrack.setChecked(tempData.enableSoundTrack);
        dubbing.setChecked(tempData.enableDubbing);
        filter.setChecked(tempData.enableFilter);
        titling.setChecked(tempData.enableTitling);
        specialEffects.setChecked(tempData.enableSpecialEffects);
        clipEditing.setChecked(tempData.enableClipEditing);

        videoTrailer.setChecked(tempData.enableVideoTrailer);
        watermark.setChecked(tempData.enableWatermark);
        textWatermark.setChecked(tempData.enableTextWatermark);
        mv.setChecked(tempData.enableMV);

        proportion.setEnabled(!tempData.enableMV);

        rbPorportionSquare.setEnabled(!tempData.enableMV);
        rbPorportionAuto.setEnabled(!tempData.enableMV);
        rbPorportionLandscape.setEnabled(!tempData.enableMV);
        setClipEditingMudulesEnabled(tempData.enableClipEditing);
        subTitleSpecialOuter.setChecked(tempData.enableTitlingAndSpecialEffectOuter);

    }

    private OnCheckedChangeListener UICheckListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            int id = buttonView.getId();

            // 片段编辑
            if (id == R.id.imageDurationControl) {
                tempData.enableImageDuration = isChecked;
            } else if (id == R.id.edit) {
                tempData.enableEdit = isChecked;
            } else if (id == R.id.trim) {
                tempData.enableTrim = isChecked;
            } else if (id == R.id.videoSpeedControl) {
                tempData.enableVideoSpeed = isChecked;
            } else if (id == R.id.split) {
                tempData.enableSplit = isChecked;
            } else if (id == R.id.copy) {
                tempData.enableCopy = isChecked;
            } else if (id == R.id.proportion) {
                tempData.enableProportion = isChecked;
            } else if (id == R.id.sort) {
                tempData.enableSort = isChecked;
            } else if (id == R.id.text) {
                tempData.enableText = isChecked;
            } else if (id == R.id.reverse) {
                tempData.enableReverse = isChecked;
            } else if (id == R.id.mv) {
                tempData.enableMV = isChecked;
                proportion.setEnabled(!isChecked);
                proportion.setChecked(!isChecked);
                if (!tempData.enableClipEditing) {
                    proportion.setEnabled(false);
                }
                rgProportion.check(R.id.rbProportionSquare);
                rbPorportionSquare.setEnabled(!isChecked);
                rbPorportionAuto.setEnabled(!isChecked);
                rbPorportionLandscape.setEnabled(!isChecked);
            }
            // 编辑与导出
            else if (id == R.id.soundTrack) {
                tempData.enableSoundTrack = isChecked;
                if (!isChecked) {
                    rgVoice.check(R.id.rbVoiceTrak1);
                }
            } else if (id == R.id.enableLocalMusic) {
                tempData.enableLocalMusic = isChecked;
            } else if (id == R.id.filter) {
                tempData.enableFilter = isChecked;
                View v = (View) buttonView.getParent();
                v.findViewById(R.id.rbFilter1).setEnabled(isChecked);
                v.findViewById(R.id.rbFilter2).setEnabled(isChecked);
                v.findViewById(R.id.rbFilter3).setEnabled(isChecked);

            } else if (id == R.id.VoiceTrack) {
                tempData.enableDubbing = isChecked;
            } else if (id == R.id.titling) {
                tempData.enableTitling = isChecked;
            } else if (id == R.id.specialEffects) {
                tempData.enableSpecialEffects = isChecked;
            } else if (id == R.id.clipEditing) {
                tempData.enableClipEditing = isChecked;
                setClipEditingMudulesEnabled(isChecked);
            }
            // 视频水印
            else if (id == R.id.watermark) {
                tempData.enableWatermark = isChecked;
                if (tempData.enableWatermark) {
                    textWatermark.setChecked(false);
                }
            }
            // 文字水印
            else if (id == R.id.textWatermark) {
                tempData.enableTextWatermark = isChecked;
                if (tempData.enableTextWatermark) {
                    watermark.setChecked(false);
                }
            }
            // 片尾水印
            else if (id == R.id.videoTrailer) {
                tempData.enableVideoTrailer = isChecked;
            } else if (id == R.id.subTitleSpecialEffectOuter) {
                tempData.enableTitlingAndSpecialEffectOuter = isChecked;
            } else if (id == R.id.ckNewApi) {
                //新的网络接口
                tempData.enableNewApi = isChecked;
                initFilterState();
            }

        }
    };

    private void setClipEditingMudulesEnabled(boolean enable) {
        imageDuration.setEnabled(enable);
        edit.setEnabled(enable);
        trim.setEnabled(enable);
        videoSpeed.setEnabled(enable);
        split.setEnabled(enable);
        copy.setEnabled(enable);
        proportion.setEnabled(enable);
        sort.setEnabled(enable);
        text.setEnabled(enable);
        reverse.setEnabled(enable);
        if (tempData.enableMV) {
            proportion.setEnabled(false);
        }
    }

    /**
     * 保存UI设置对话框的参数
     */
    private void saveUIConfigData() {
        // 读取导出时间限制
        if (etExportDuration != null) {
            if (!TextUtils.isEmpty(etExportDuration.getText())) {
                tempData.exportVideoDuration = Float.valueOf(etExportDuration
                        .getText().toString());
            } else {
                tempData.exportVideoDuration = 0;
            }
        }
        // 水印显示位置
        if (etWatermarkXAxis != null) {
            float left, top, right, bottom;
            if (!TextUtils.isEmpty(etWatermarkXAxis.getText())) {
                left = Float.valueOf(etWatermarkXAxis.getText().toString());
            } else {
                left = 0;
            }
            if (!TextUtils.isEmpty(etWatermarkYAxis.getText())) {
                top = Float.valueOf(etWatermarkYAxis.getText().toString());
            } else {
                top = 0;
            }
            if (!TextUtils.isEmpty(etWatermarkXZoom.getText())) {
                right = Float.valueOf(etWatermarkXZoom.getText().toString());
            } else {
                right = 0;
            }
            if (!TextUtils.isEmpty(etWatermarkYZoom.getText())) {
                bottom = Float.valueOf(etWatermarkYZoom.getText().toString());
            } else {
                bottom = 0;
            }
            tempData.watermarkShowRectF.set(left, top, right, bottom);
        }
        if (tempData.enableVideoTrailer) {
            //构造片尾
            tempData.videoTrailerPath = SDKUtils.createVideoTrailerImage(
                    mActitivy, mActitivy.getString(R.string.trailerAuthor), 480, 50, 50);
        } else {
            tempData.videoTrailerPath = null;
        }

        String str = etCustomApi.getText().toString();

        tempData.customApi = str.trim();
    }
}
