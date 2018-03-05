package com.xhrd.mobile.hybrid.framework.manager.audio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.Manifest;
import android.util.SparseArray;

import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.engine.HybridView;
import com.xhrd.mobile.hybrid.engine.HybridResourceManager;
import com.xhrd.mobile.hybrid.framework.PluginBase;
import com.xhrd.mobile.hybrid.framework.PluginData;

/**
 * 音频管理器
 */
public class Audio extends PluginBase {
    /**
     * 录音器js
     */
    private static final String JSON_RECORDER =
            "{id:%d," +
            "hostId:%d," +
            "record:function(recordOpts, sucFunc, errFunc){" +
                "if (arguments == null || arguments.length == 0)" +
                    "return; " +
                "exec('RDCloud://" + Audio.class.getName() + "/recorderCall/'+this.hostId, ['record',this.id,recordOpts, sucFunc, errFunc], false, false);" +
            "}, " +
            "playJustRecord:function(){" +
                "exec('RDCloud://" + Audio.class.getName() + "/recorderCall/'+this.hostId, ['playJustRecord',this.id], false, false);" +
            "}, " +
            "getCurrentTime:function(){" +
                "var size = exec('RDCloud://" + Audio.class.getName() + "/recorderCall/'+this.hostId, ['getCurrentTime',this.id], true, true);" +
                    "return size;" +
            "}, " +
            "getDuration:function(){" +
                "var size = exec('RDCloud://" + Audio.class.getName() + "/recorderCall/'+this.hostId, ['getDuration',this.id], true, true);" +
                    "return size;" +
            "}, " +
            "addAveragePowerCallback:function(sucFunc){" +
                "if (arguments == null || arguments.length == 0)" +
                    "return; " +
                "exec('RDCloud://" + Audio.class.getName() + "/recorderCall/'+this.hostId, ['addAveragePowerCallback',this.id,sucFunc], false, false);" +
            "}, " +
            "addCountDownCallback:function(sucFunc){" +
                "if (arguments == null || arguments.length == 0)" +
                    "return; " +
                "exec('RDCloud://" + Audio.class.getName() + "/recorderCall/'+this.hostId, ['addCountDownCallback',this.id,sucFunc], false, false);" +
            "}, " +
            "addCalculateTimeCallback:function(sucFunc){" +
                 "if (arguments == null || arguments.length == 0)" +
                    "return; " +
                 "exec('RDCloud://" + Audio.class.getName() + "/recorderCall/'+this.hostId, ['addCalculateTimeCallback',this.id,sucFunc], false, false);" +
            "}, " +
            "stop:function(){" +
                "exec('RDCloud://" + Audio.class.getName() + "/recorderCall/'+this.hostId, ['stop',this.id], false, false);" +
            "}}";

    /**
     * 音频播放器js
     */
    private static final String JSON_PLAYER =
            "{ROUTE_SPEAKER:" + Player.ROUTE_SPEAKER + ", " +
            "ROUTE_EARPIECE:" + Player.ROUTE_EARPIECE + ", " +
            "id:%d," +
            "hostId:%d," +
            "play:function(sucFunc, errFunc){" +
                "exec('RDCloud://" + Audio.class.getName() + "/playerCall/'+this.hostId, ['play',this.id, sucFunc, errFunc], false, false);" +
                //"rd.AudioManager.playerCall('play', %d, ja2sa(arguments));" +
            "}, " +
            "setRoute:function(){" +
                "if (arguments == null || arguments.length == 0)" +
                    "return; " +
                    "exec('RDCloud://" + Audio.class.getName() + "playerCall/'+this.hostId, ['setRoute',this.id,arguments[0]], false, false);" +
                    //"rd.AudioManager.playerCall('setRoute', %d, ja2sa(arguments));" +
            "}, " +
            "seekTo:function(){" +
            	"if (arguments == null || arguments.length == 0)" +
                	"return; " +
                "exec('RDCloud://" + Audio.class.getName() + "/playerCall/'+this.hostId, ['seekTo',this.id,arguments[0]], false, false);" +
                //"rd.AudioManager.playerCall('seekTo', %d, ja2sa(arguments));" +
            "}, " +
            "getMode:function(){" +
                //"var ret = rd.AudioManager.playerCall('getMode', %d, ja2sa(arguments));" +
                "var ret = exec('RDCloud://" + Audio.class.getName() + "/playerCall/'+this.hostId, ['getMode',this.id], true, false);" +
                "return parseInt(ret);" +
            "}, " +
            "pause:function(){" +
                "exec('RDCloud://" + Audio.class.getName() + "/playerCall/'+this.hostId, ['pause',this.id], false, false);" +
                //"rd.AudioManager.playerCall('pause', %d, ja2sa(arguments));" +
            "}, " +
            "resume:function(){" +
                "exec('RDCloud://" + Audio.class.getName() + "/playerCall/'+this.hostId, ['resume',this.id], false, false);" +
                //"rd.AudioManager.playerCall('resume', %d, ja2sa(arguments));" +
            "}, " +
            "getDuration:function(){" +
                "var size = exec('RDCloud://" + Audio.class.getName() + "/playerCall/'+this.hostId, ['getDuration',this.id], true, false);" +
                "console.log('size: ' + size);" +
                "return size;" +
        	"}, " +
        	"getPosition:function(){" +
                "var time = exec('RDCloud://" + Audio.class.getName() + "/playerCall/'+this.hostId, ['getPosition',this.id], true, false);" +
                "return time;" +
    		"}, " +
            "isPlaying:function(){" +
                "var b = exec('RDCloud://" + Audio.class.getName() + "/playerCall/'+this.hostId, ['isPlaying',this.id], true, true);" +
                "return b == '1';" +
    		"}, " +
            "stop:function(){" +
                "exec('RDCloud://" + Audio.class.getName() + "/playerCall/'+this.hostId, ['stop',this.id], false, false);" +
                //"rd.AudioManager.playerCall('stop', %d, ja2sa(arguments));" +
            "}}";
    private AtomicInteger mInteger;
    private SparseArray<Recorder> mRecorderList;
    private SparseArray<Player> mPlayerList;
    public Audio() {
        mInteger = new AtomicInteger();
        mRecorderList = new SparseArray<Recorder>();
        mPlayerList = new SparseArray<Player>();
    }

    @Override
    public String getDefaultDomain() {
        return "audio";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDeregistered(HybridView view) {
        super.onDeregistered(view);
        for (int i = 0; i < mRecorderList.size(); i++) {
            mRecorderList.valueAt(i).stop(view);
        }
        mRecorderList.clear();
        for (int i = 0; i < mPlayerList.size(); i++) {
            mPlayerList.valueAt(i).stop();
        }
        mPlayerList.clear();
    }

    @JavascriptFunction
    public String getRecorder(HybridView rdCloudView, String[] params) {
        int recorderId = mInteger.getAndIncrement();
        Recorder recorder = new Recorder(this);
        mRecorderList.put(recorderId, recorder);
//        Log.e("getRecorder--------->", String.format(JSON_RECORDER, recorderId, recorderId));
//        String supportedSamplerates = "[95000,44100]";
//        String supportedFormats = "[aac,amr]";
//        JSONArray array = null;
//        JSONArray array1 = null;
//        try {
//            array = new JSONArray(supportedSamplerates);
//            array1 = new JSONArray(supportedFormats);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        return String.format(JSON_RECORDER, recorderId, recorderId);
    }

    @JavascriptFunction
    public String recorderCall(HybridView rdCloudView, String[] params) {
        String method = params[0];
        int id = Integer.parseInt(params[1]);
        if ("record".equals(method)) {
            Recorder recorder = mRecorderList.get(id);
            if (recorder == null){
                recorder = new Recorder(this);
                mRecorderList.put(id, recorder);
            }
            recorder.record(rdCloudView,params);
        } else if ("stop".equals(method)) {
            Recorder recorder = mRecorderList.get(id);
            recorder.stop(rdCloudView);
            mRecorderList.remove(id);
        } else if ("playJustRecord".equals(method)){
//            Recorder recorder = mRecorderList.get(id);
//            if (recorder == null){
//                recorder = new Recorder(this);
//                mRecorderList.put(id, recorder);
//            }
//            recorder.play();
            Recorder.play();
        } else if ("getCurrentTime".equals(method)){
            String currentTime = Recorder.getCurrentTime();
            return currentTime;
        } else if ("getDuration".equals(method)){
            String duration = Recorder.getDuration();
            return duration;
        } else if ("addAveragePowerCallback".equals(method)){
            Recorder recorder = mRecorderList.get(id);
            recorder.addAveragePowerCallback(rdCloudView,params);
        } else if ("addCountDownCallback".equals(method)){
            Recorder recorder = mRecorderList.get(id);
            recorder.addCountDownCallback(rdCloudView,params);
        } else if ("addCalculateTimeCallback".equals(method)){
            Recorder recorder = mRecorderList.get(id);
            recorder.addCalculateTimeCallback(rdCloudView,params);
        }
        return "";
    }

    @JavascriptFunction
    public String createPlayer(HybridView rdCloudView, String[] params) {
        Player player = new Player(rdCloudView,params[0], this);
        int id = mInteger.getAndIncrement();
        mPlayerList.put(id, player);
        return String.format(JSON_PLAYER, id, getId());
    }

    @JavascriptFunction
    public String playerCall(HybridView rdCloudView, String[] params) {
        String method = params[0];
        int id = Integer.parseInt(params[1]);
        if (params.length > 2) {
            List<String> pList = new ArrayList<String>(Arrays.asList(params));
            pList.remove(0);
            pList.remove(0);
            params = pList.toArray(new String[0]);
        }
        if ("play".equals(method)) {
            Player player = mPlayerList.get(id);
            player.play(params);
            //Log.i("---------->",getDefaultDomain());
        } else if ("stop".equals(method)) {
            Player player = mPlayerList.get(id);
            player.stop();
        } else if ("setRoute".equals(method)) {
            Player player = mPlayerList.get(id);
            player.setRoute(params);
        } else if ("getMode".equals(method)) {
            Player player = mPlayerList.get(id);
            return player.getAudioMode() + "";
        } else if ("pause".equals(method)) {
        	Player player = mPlayerList.get(id);
            player.pause();
		} else if ("resume".equals(method)) {
			Player player = mPlayerList.get(id);
            player.resume();
		} else if ("getDuration".equals(method)) {
			Player player = mPlayerList.get(id);
            String size = player.getDuration();
            return size;
		} else if ("getPosition".equals(method)) {
			Player player = mPlayerList.get(id);
			String currentTime = player.getPosition();
			return currentTime;
		} else if ("seekTo".equals(method)) {
			Player player = mPlayerList.get(id);
            player.seekTo(params);
		} else if ("isPlaying".equals(method)) {
            Player player = mPlayerList.get(id);
            return player.isPlaying() + "";
        }
        return "";
    }

    @Override
    public void addMethodProp(PluginData data) {
        data.addMethod("getRecorder", null, false , true);
        data.addMethod("recorderCall", new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new String[]{HybridResourceManager.getInstance().getString("request_audio_permission_msg")});
        data.addMethod("createPlayer", null, false , true,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, new String[]{HybridResourceManager.getInstance().getString("request_audio_permission_msg")});
        data.addMethod("playerCall", new String[]{"method", "id"}, true, true);
        data.addProperty("ROUTE_SPEAKER", 0);
        data.addProperty("ROUTE_EARPIECE", 1);
    }

    @Override
    public PluginData.Scope getScope() {
        return PluginData.Scope.App;
    }
}
