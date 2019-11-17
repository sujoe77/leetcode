package com.pineapple.java.redis.ch09;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

public class StringUtil {
    static final String[] COUNTRIES = (
            "ABW AFG AGO AIA ALA ALB AND ARE ARG ARM ASM ATA ATF ATG AUS AUT AZE BDI " +
                    "BEL BEN BES BFA BGD BGR BHR BHS BIH BLM BLR BLZ BMU BOL BRA BRB BRN BTN " +
                    "BVT BWA CAF CAN CCK CHE CHL CHN CIV CMR COD COG COK COL COM CPV CRI CUB " +
                    "CUW CXR CYM CYP CZE DEU DJI DMA DNK DOM DZA ECU EGY ERI ESH ESP EST ETH " +
                    "FIN FJI FLK FRA FRO FSM GAB GBR GEO GGY GHA GIB GIN GLP GMB GNB GNQ GRC " +
                    "GRD GRL GTM GUF GUM GUY HKG HMD HND HRV HTI HUN IDN IMN IND IOT IRL IRN " +
                    "IRQ ISL ISR ITA JAM JEY JOR JPN KAZ KEN KGZ KHM KIR KNA KOR KWT LAO LBN " +
                    "LBR LBY LCA LIE LKA LSO LTU LUX LVA MAC MAF MAR MCO MDA MDG MDV MEX MHL " +
                    "MKD MLI MLT MMR MNE MNG MNP MOZ MRT MSR MTQ MUS MWI MYS MYT NAM NCL NER " +
                    "NFK NGA NIC NIU NLD NOR NPL NRU NZL OMN PAK PAN PCN PER PHL PLW PNG POL " +
                    "PRI PRK PRT PRY PSE PYF QAT REU ROU RUS RWA SAU SDN SEN SGP SGS SHN SJM " +
                    "SLB SLE SLV SMR SOM SPM SRB SSD STP SUR SVK SVN SWE SWZ SXM SYC SYR TCA " +
                    "TCD TGO THA TJK TKL TKM TLS TON TTO TUN TUR TUV TWN TZA UGA UKR UMI URY " +
                    "USA UZB VAT VCT VEN VGB VIR VNM VUT WLF WSM YEM ZAF ZMB ZWE").split(" ");
    static final Map<String, String[]> STATES = new HashMap<String, String[]>();

    public static void updateAggregates(Map<String, Long> countries, Map<String, Map<String, Long>> states, List<Object> codes) {
        for (Object code : codes) {
            updateAggregates(countries, states, (String) code);
        }
    }

    public static void updateAggregates(Map<String, Long> countries, Map<String, Map<String, Long>> states, String code) {
        if (code.length() != 2) {
            return;
        }

        int countryIdx = (int) code.charAt(0) - 1;
        int stateIdx = (int) code.charAt(1) - 1;

        if (countryIdx < 0 || countryIdx >= COUNTRIES.length) {
            return;
        }

        String country = COUNTRIES[countryIdx];
        Long countryAgg = countries.get(country);
        if (countryAgg == null) {
            countryAgg = Long.valueOf(0);
        }
        countries.put(country, countryAgg + 1);

        if (!STATES.containsKey(country)) {
            return;
        }
        if (stateIdx < 0 || stateIdx >= STATES.get(country).length) {
            return;
        }

        String state = STATES.get(country)[stateIdx];
        Map<String, Long> stateAggs = states.get(country);
        if (stateAggs == null) {
            stateAggs = new HashMap<String, Long>();
            states.put(country, stateAggs);
        }
        Long stateAgg = stateAggs.get(state);
        if (stateAgg == null) {
            stateAgg = Long.valueOf(0);
        }
        stateAggs.put(state, stateAgg + 1);
    }

    public static String getCode(String country, String state) {
        int cindex = bisectLeft(COUNTRIES, country);
        if (cindex > COUNTRIES.length || !country.equals(COUNTRIES[cindex])) {
            cindex = -1;
        }
        cindex++;

        int sindex = -1;
        if (state != null && STATES.containsKey(country)) {
            String[] states = STATES.get(country);
            sindex = bisectLeft(states, state);
            if (sindex > states.length || !state.equals(states[sindex])) {
                sindex--;
            }
        }
        sindex++;

        return new String(new char[]{(char) cindex, (char) sindex});
    }

    public static int bisectLeft(String[] values, String key) {
        int index = Arrays.binarySearch(values, key);
        return index < 0 ? Math.abs(index) - 1 : index;
    }

    public static boolean isDigit(String string) {
        for (char c : string.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

}
