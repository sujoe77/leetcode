package com.pineapple.java.redis.ch07.entity;

public enum Ecpm {
    CPC, CPA, CPM;

    public static double toEcpm(Ecpm type, double views, double avg, double value) {
        switch (type) {
            case CPC:
            case CPA:
                return 1000. * value * avg / views;
            case CPM:
                return value;
        }
        return value;
    }
}
