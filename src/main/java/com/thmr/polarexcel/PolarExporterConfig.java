package com.thmr.polarexcel;

import java.util.HashMap;
import java.util.Map;

import com.thmr.polar.Config;

public class PolarExporterConfig
{
	public static String SPORT_ID_RUNNING_TRANSLATION_KEY = "sport_id_running";
	public static String SPORT_ID_MOUNTAIN_BIKING_TRANSLATION_KEY = "sport_id_mountain_biking";
	public static String SPORT_ID_STRENGTH_TRAINING_TRANSLATION_KEY = "sport_id_strength_training";
	public static String SPORT_ID_OTHER_OUTDOOR_TRANSLATION_KEY = "sport_id_other_outdoor";
	public static String SPORT_ID_INDOOR_CYCLING_TRANSLATION_KEY = "sport_id_indoor_sycling";
	public static String SPORT_ID_INLINE_SKATING_TRANSLATION_KEY = "sport_id_inline_skating";
	public static String SPORT_ID_ROAD_CYCLING_TRANSLATION_KEY = "sport_id_road_cycling";
	public static String SPORT_ID_CYCLING_TRANSLATION_KEY = "sport_id_cycling";

	public static String DISTANCE_TRANSLATION_KEY = "distance";
	public static String DURATION_TRANSLATION_KEY = "duration";
	
	public static final Map<Integer, String> sportIdTranslationKeyMapping = new HashMap<Integer, String>();

	static
	{
		sportIdTranslationKeyMapping.put(Config.SPORT_ID_RUNNING, SPORT_ID_RUNNING_TRANSLATION_KEY);
		sportIdTranslationKeyMapping.put(Config.SPORT_ID_MOUNTAIN_BIKING, SPORT_ID_MOUNTAIN_BIKING_TRANSLATION_KEY);
		sportIdTranslationKeyMapping.put(Config.SPORT_ID_STRENGTH_TRAINING, SPORT_ID_STRENGTH_TRAINING_TRANSLATION_KEY);
		sportIdTranslationKeyMapping.put(Config.SPORT_ID_OTHER_OUTDOOR, SPORT_ID_OTHER_OUTDOOR_TRANSLATION_KEY);
		sportIdTranslationKeyMapping.put(Config.SPORT_ID_INDOOR_CYCLING, SPORT_ID_INDOOR_CYCLING_TRANSLATION_KEY);
		sportIdTranslationKeyMapping.put(Config.SPORT_ID_INLINE_SKATING, SPORT_ID_INLINE_SKATING_TRANSLATION_KEY);
		sportIdTranslationKeyMapping.put(Config.SPORT_ID_ROAD_CYCLING, SPORT_ID_ROAD_CYCLING_TRANSLATION_KEY);
		sportIdTranslationKeyMapping.put(Config.SPORT_ID_CYCLING, SPORT_ID_CYCLING_TRANSLATION_KEY);
	}
}
