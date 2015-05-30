package ac.at.tuwien.sbc.investor;

import java.util.ArrayList;

/**
 * Created by dietl_ma on 29/05/15.
 */
public class InitArgs {

    private ArrayList<InitArg> args;

    public InitArgs(String[] args) {
        this.args = new ArrayList<InitArg>();
        for (String arg : args)
            this.args.add(new InitArg(arg));
    }

    public ArrayList<InitArg> getArgs() {
        return args;
    }

    public void setArgs(ArrayList<InitArg> args) {
        this.args = args;
    }

    public class InitArg {

        private String market;

        private Double budget;

        private Integer numShares;

        public InitArg(String arg) {

            String[] args = arg.split("\\|");
            this.market = args[0];
            this.budget = Double.valueOf(args[1]);
            this.numShares = (args.length > 2) ? Integer.valueOf(args[2]) : null;
        }

        public String getMarket() {
            return market;
        }

        public void setMarket(String market) {
            this.market = market;
        }

        public Double getBudget() {
            return budget;
        }

        public void setBudget(Double budget) {
            this.budget = budget;
        }

        public Integer getNumShares() {
            return numShares;
        }

        public void setNumShares(Integer numShares) {
            this.numShares = numShares;
        }
    }

}
