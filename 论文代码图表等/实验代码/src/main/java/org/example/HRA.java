package org.example;

import java.util.*;

public class HRA {


    /**
     * 高斯分布
     * @param mean
     * @param stdDev
     * @return
     */
    public static double next(double mean, double stdDev) {
        // Box-Muller转换方法生成高斯随机数
        Random random = new Random();
        double u1 = random.nextDouble();
        double u2 = random.nextDouble();
//        double randStdNormal = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
//        return mean + stdDev * randStdNormal;
        return mean + stdDev * random.nextGaussian();
    }

    /**
     * DDP-HRA的算法一
     * @param data
     * @param K
     * @param epsilon
     * @param zeita
     * @return
     */
    public ArrayList<int[]> kwitSortpart1(List<ArrayList<Integer>>data, int K, double epsilon , double zeita ){
        int m = data.get(0).size();
        double epsolon1 = epsilon/K;
        ArrayList<int[]>res = new ArrayList<>();
        for(ArrayList<Integer> linedata : data){
            HashMap<Integer, Integer> map = new HashMap<>();
            for(int i =0; i<linedata.size(); i++)
                map.put(linedata.get(i)-1, i); //!!!!!!!!!!!!!!!!!!
            for(int i=0; i<K; i++) {
                Random random = new Random();
                int j = random.nextInt(m);  //生成0——m-1之间的整数
                int k = random.nextInt(m);
                while (k == j)   //确保jk不相同
                    k = random.nextInt(m);
                double lij = map.get(j)>map.get(k)?1:0;
                //添加高斯噪声
                double guess = HRA.next(0, K*Math.sqrt(2*Math.log(1.25/zeita))/epsilon );
                lij += guess;

                if(lij > 0.5)
                    lij = 1;
                else
                    lij = 0;
//                System.out.println(lij+"   "+j+"    "+k);
                res.add(new int[]{(int) lij, j, k});
            }

        }
        return res;
    }



    /**
     * DDP-HRA的算法二
     * @param data
     * @param R
     * @return
     */
    public ArrayList<Integer> kwitSortpart2(List<ArrayList<Integer>>data, ArrayList<int[]>R){
        int m = data.get(0).size();
        ArrayList<Integer>res =  new ArrayList<Integer>();
        double C[][]  = new double[m][m];
        double PCM[][]  = new double[m][m];
        double PPR[][]  = new double[m][m];
        for(int[]lij : R){
            if(lij[0] == 1)
                C[lij[1]][lij[2]] += 1;
            else
                C[lij[2]][lij[1]] += 1;
        }
        for(int i=0; i<m; i++){
            for(int j=0; j<m; j++){
                if(i == j)continue;
                PCM[i][j] = C[i][j]/(C[i][j]+C[j][i]);
//                System.out.println(PCM[i][j]+"   "+i+"   "+j);
            }
        }
        for(int i=0; i<m; i++){
            for(int j=0; j<m; j++){
                if(i == j)continue;
                PPR[i][j] = PCM[i][j];
//                System.out.println("PCM:   "+ PCM[i][j]+"   "+i+"  "+j);
                if(PCM[i][j] == PCM[j][i])
                    PPR[i][j] = 0.5;
                else if(PCM[i][j] > PCM[j][i])
                    PPR[i][j] = 1;
                else
                    PPR[i][j] = 0;
                PPR[i][j] = PCM[i][j];
            }
        }
        double[] L = new double[m];
        for(int j=0; j<m; j++){
            for (int i=0; i<m; i++)
                L[j] += PPR[i][j];
        }
//        for(int i=0; i<m; i++)
//            System.out.println(L[i]+"  "+i);
        double[][]ans = new double[m][2];
        for(int i=0; i<m; i++){
            ans[i][0] = L[i];
            ans[i][1] = i+1;
        }
        Arrays.sort(ans, ((o1, o2) -> o2[0]>o1[0]?1:-1));
//        ArrayList<Integer>res =new ArrayList<>();
        for(int i=0; i<m ; i++)
            res.add((int) ans[i][1]);
//        System.out.println(res);
        return res;
    }



    public static void main(String[] args) {
        HRA kwitSort = new HRA();
//        System.out.println( kwitSort.next(0, 0.001) );

    }

}
