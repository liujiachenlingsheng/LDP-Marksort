package org.example;

import java.util.*;

public class HelnakSort {


    /**
     * DDP-HRA的算法一
     * @param data
     * @param K
     * @param epsilon
     * @return
     */
    public ArrayList<int[]> HelnakSortPart1(List<ArrayList<Integer>> data, int K, double epsilon ){
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
//                double guess = HRA.next(0, K*Math.sqrt(2*Math.log(1.25/zeita))/epsilon );
                double r = Math.random();
//                r = random.nextInt();
                double p = Math.pow( Math.E, epsilon/K)/(Math.pow( Math.E, epsilon/K)+1);
                if(r > p) {   //反转加噪声
                    lij = (lij + 1) % 2;
                }
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
    public ArrayList<Integer> HelnakSortPart2(List<ArrayList<Integer>>data, ArrayList<int[]>R){
        int m = data.get(0).size();
        ArrayList<Integer>res =  new ArrayList<Integer>();
        double C[][]  = new double[m][m];
        double PCM[][]  = new double[m][m];
        double PPR[][]  = new double[m][m];
        double Ca[] = new double[m];
        for(int[]lij : R){
            if(lij[0] == 1)
                C[lij[1]][lij[2]] += 1;
            else
                C[lij[2]][lij[1]] += 1;
        }
        for(int i=0; i<m; i++){
            for(int j=0; j<m; j++){
                Ca[i] += (C[i][j]-C[j][i]);
            }
        }

        for(int i=0; i<m; i++){
            for(int j=0; j<m; j++){
                if(i == j)continue;
                PCM[i][j] = C[i][j]/(C[i][j]+C[j][i]);
            }
        }

        for(int i=0; i<m; i++){
            for(int j=0; j<m; j++){
                if(i == j)continue;
                if(PCM[i][j] == PCM[j][i])
                    PPR[i][j] = 0.5;
                else if(PCM[i][j] > PCM[j][i])
                    PPR[i][j] = 1;
                else
                    PPR[i][j] = 0;
            }
        }
        //L[j]会有很多相同的，先分组然后载根据C来排序
        int[]rank  = new int[m];
        for (int i = 0; i < m; i++) {
            rank[i] = i;
        }
        int[] helnaksort = Helnaksort(PPR, rank, Ca, 0, m - 1);

//        double[] L = new double[m];
//        for(int j=0; j<m; j++){
//            for (int i=0; i<m; i++)
//                L[j] += PPR[i][j];
////            System.out.print(L[j]+"  ");
//        }
//
//        double[][]ans = new double[m][3];
//        for(int i=0; i<m; i++){
//            ans[i][0] = L[i];
//            ans[i][1] = i+1;
//            ans[i][2] = Ca[i];
//        }
//        Arrays.sort(ans, ((o1, o2) -> o2[0]>o1[0]?1:(o2[0]<o1[0]?-1:(o2[2]>o1[2]?1:-1) ) ));
////        Arrays.sort(ans, ((o1, o2) -> o2[0]>o1[0]?1: -1 ));
//        ArrayList<Integer>res =new ArrayList<>();
        for(int i=0; i<m ; i++)
            res.add((int) helnaksort[i]+1);

        return res;
    }

    public int[] Helnaksort(double[][]PPR, int[]rank, double[]Ca, int start, int end){
        int m = PPR.length;
        double[][] L = new double[end-start+1][2];   //第一个参数是分数L， 第二个是选项编号
        for(int j=start; j<=end; j++){
            L[j-start][1] = rank[j];
            for (int i=start; i<=end; i++)
                L[j-start][0] += PPR[rank[i]][rank[j]];
        }
        Arrays.sort(L,((o1, o2) -> o2[0]>o1[0]?1: -1));

        int cot = 1;
        for (int i = 1; i < end-start+1; i++) {
            if(L[i][0] == L[i-1][0])
                cot++;
        }
        int sign = 0;
        if(cot == end-start+1) {  //如果全部相等
            sign = 1;    //如果全部相等，第一个位置排好好了
            double max = Ca[rank[start]], index  = start;
            for (int i = start+1; i <=end ; i++) {
                double a  = Ca[rank[i]];
                if(a > max){
                    a = max;
                    index = i;
                }
            }
            // change   index & start
            index -= start;
            if(index != start){
                double temp = L[(int) index][0];
                L[(int) index][0] =  L[0][0];
                L[0][0] = L[(int) index][0];
                temp = L[(int) index][1];
                L[(int) index][1] =  L[0][1];
                L[0][1] = L[(int) index][1];
            }
        }
        for (int i = start; i <= end ; i++) {
            rank[i] = (int) L[i-start][1];
        }

        cot = 1;
        for (int i = 1+sign; i < end-start+1; i++) {
            if(L[i][0] == L[i-1][0])cot++;
            else if(cot > 1) {
                Helnaksort(PPR, rank, Ca, start+i-cot,start+i-1);
                cot = 1;
            }
        }
        if(cot > 1)
            Helnaksort(PPR, rank, Ca, end-cot+1, end);
        return rank;
    }


}
