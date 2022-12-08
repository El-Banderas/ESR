package otherServer.Bootstrapper;

import Common.InfoNodo;

import java.util.Objects;

public class Connection implements Comparable<Connection>{
    public InfoNodo from;
    public InfoNodo to;
    public double delay;
    public int numHops;

    public Connection(InfoNodo from, InfoNodo to, double delay, int numHops) {
        this.from = from;
        this.to = to;
        this.delay = delay;
        this.numHops = numHops;
    }

    public InfoNodo getFrom() {
        return from;
    }

    public void setFrom(InfoNodo from) {
        this.from = from;
    }

    public InfoNodo getTo() {
        return to;
    }

    public void setTo(InfoNodo to) {
        this.to = to;
    }

    public double getDelay() {
        return delay;
    }

    public void setDelay(double delay) {
        this.delay = delay;
    }

    public int getNumHops() {
        return numHops;
    }

    public void setNumHops(int numHops) {
        this.numHops = numHops;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return Double.compare(that.delay, delay) == 0 && numHops == that.numHops && from.equals(that.from) && to.equals(that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, delay, numHops);
    }

    @Override
    public String toString() {
        return "Connection{" +
                from.toString() +
                " ----> " + to.toString() +
                "|| delay=" + delay +
                " || numHops=" + numHops +
                '}';
    }


    public int compareTo(Connection that) {
        return (int) ((this.delay*0.6 + this.numHops*0.4 )- (that.delay*0.6 + that.numHops*0.4));
    }
}

