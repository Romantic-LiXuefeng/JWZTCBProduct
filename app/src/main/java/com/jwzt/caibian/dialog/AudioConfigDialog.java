package com.jwzt.caibian.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.jwzt.cb.product.R;


/**
 * 音频设置对话框
 */
public class AudioConfigDialog {
    private AlertDialog dialog;
    private EditText etAudioNumChannel, etAudioSampleRate, etAudioBitRate;
    public static int audioNumChannel, audioSampleRate, audioBitRate;

    /**
     * 构造函数
     */
    public AudioConfigDialog(Activity activity, final ConfigDialogListener listener) {
        View view = activity.getLayoutInflater().inflate(
                R.layout.audio_config_dialog, null);
        dialog = new AlertDialog.Builder(activity)
                .setView(view)
                .setPositiveButton(activity.getString(R.string.back), null)
                .setNegativeButton(activity.getString(R.string.save), new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveConfigData();
                    }
                }).show();
        dialog.setOnDismissListener(listener);
        initDialogView(view);
    }

    /**
     * 初始化拍摄设置对话框
     *
     * @param view
     */
    private void initDialogView(View view) {
        etAudioNumChannel = (EditText) view.findViewById(R.id.etNumChannels);
        if (audioNumChannel != 0) {
            etAudioNumChannel.setText("" + audioNumChannel);
        }
        etAudioSampleRate = (EditText) view.findViewById(R.id.etSampleRate);
        if (audioSampleRate != 0) {
            etAudioSampleRate.setText("" + audioSampleRate);
        }
        etAudioBitRate = (EditText) view.findViewById(R.id.etAudioBitRate);
        if (audioBitRate != 0) {
            etAudioBitRate.setText("" + audioBitRate);
        }
    }

    /**
     * 保存设置对话框的参数
     */
    private void saveConfigData() {
        if (etAudioNumChannel != null) {
            if (!TextUtils.isEmpty(etAudioNumChannel.getText())) {
                audioNumChannel = Integer
                        .valueOf(etAudioNumChannel.getText().toString());
            } else {
                audioNumChannel = 0;
            }
        }
        if (etAudioSampleRate != null) {
            if (!TextUtils.isEmpty(etAudioSampleRate.getText())) {
                audioSampleRate = Integer
                        .valueOf(etAudioSampleRate.getText().toString());
            } else {
                audioSampleRate = 0;
            }
        }
        if (etAudioBitRate != null) {
            if (!TextUtils.isEmpty(etAudioBitRate.getText())) {
                audioBitRate = Integer
                        .valueOf(etAudioBitRate.getText().toString());
            } else {
                audioBitRate = 0;
            }
        }
    }
}
