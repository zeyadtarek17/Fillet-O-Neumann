public class Register {

    private int value;
    String name;
    static int count=0;
    

    public Register() {
        this.value = 0;
        name= "R"+ count;
        count++;

    }

    public Register(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
}
