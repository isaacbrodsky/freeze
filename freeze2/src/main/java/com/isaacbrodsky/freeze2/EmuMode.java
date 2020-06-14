package com.isaacbrodsky.freeze2;

public enum EmuMode {
    ZZT {
        @Override
        public String getWorldFileSuffix() {
            return "zzt";
        }

        @Override
        public String getHiscoreFileSuffix() {
            return "hi";
        }

        @Override
        public int getHiscoreNameSize() {
            return 50;
        }
    },
    SUPERZZT {
        @Override
        public String getWorldFileSuffix() {
            return "szt";
        }

        @Override
        public String getHiscoreFileSuffix() {
            return "hgz";
        }

        @Override
        public int getHiscoreNameSize() {
            return 60;
        }
    };

    public abstract String getWorldFileSuffix();
    public abstract String getHiscoreFileSuffix();
    public abstract int getHiscoreNameSize();
}
