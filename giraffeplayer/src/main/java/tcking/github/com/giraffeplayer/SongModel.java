package tcking.github.com.giraffeplayer;

public class SongModel {
    public String DATA;
    public String DISPLAY_NAME;
    public String DURATION;
    public String SIZE;
    public String _ID;
    public int seekPostion;

    public int getSeekPostion() {
        return seekPostion;
    }

    public void setSeekPostion(int seekPostion) {
        this.seekPostion = seekPostion;
    }

    public String getDATA() {
        return DATA;
    }

    public void setDATA(String DATA) {
        this.DATA = DATA;
    }

    public String getDISPLAY_NAME() {
        return DISPLAY_NAME;
    }

    public void setDISPLAY_NAME(String DISPLAY_NAME) {
        this.DISPLAY_NAME = DISPLAY_NAME;
    }

    public String getDURATION() {
        return DURATION;
    }

    public void setDURATION(String DURATION) {
        this.DURATION = DURATION;
    }

    public String getSIZE() {
        return SIZE;
    }

    public void setSIZE(String SIZE) {
        this.SIZE = SIZE;
    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }
}
