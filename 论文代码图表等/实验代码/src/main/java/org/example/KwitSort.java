package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class KwitSort {


    /**
     * KwitSort算法一
     * @param data
     * @param K
     * @param epsilon
     * @return
     */
    public ArrayList<int[]> kwitSortpart1(List<ArrayList<Integer>> data, int K, double epsilon){
        int m = data.get(0).size();
        double epsolon1 = epsilon/K;
        double zita = 0.0001;
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
     *
     */
    public ArrayList<double[]> kwitSortpart2(List<ArrayList<Integer>>data, ArrayList<int[]>R, int K, double epsilon){
        int m = data.get(0).size();
        ArrayList<double[]>res =  new ArrayList<double[]>();
        double p = Math.pow( Math.E, epsilon/K)/(Math.pow( Math.E, epsilon/K)+1);
        double C[][]  = new double[m][m];
        double C1[][]  = new double[m][m];
        double CMP[][]  = new double[m][m];
        for(int[]lij : R){
            if(lij[0] == 1)
                C[lij[1]][lij[2]] += 1;
            else
                C[lij[2]][lij[1]] += 1;
        }
//        for(int i=0; i<m; i++){
//            for(int j=i+1; j<m; j++){
//                double x = C[i][j], y = C[j][i];
//                C1[i][j] = x*p/(2*p-1) + y*(p-1)/(2*p-1);
//                C1[j][i] = x*(p-1)/(2*p-1) + y*p/(2*p-1);
//            }
//        }
        for(int i=0; i<m; i++){
            for(int j=0; j<m; j++){
                CMP[i][j] = (C[i][j]-C[j][i])/(2*p-1);
            }
        }
        int[]rank = new int[m];
        for (int i = 0; i < m; i++) {
            rank[i] = i;
        }
        int[] kwik = Kwik(CMP, 0, m - 1, rank);
        for (int i = 0; i < m; i++)
            res.add( new double[]{kwik[i]+1, 0 });
        return res;


    }


    public static int[] Kwik(double[][] CMP,int start,int end, int[]arr) {   //arr记录排名
        Random random = new Random();
        int pivotindex = random.nextInt(end-start+1)+start;
        int pivot = arr[pivotindex];
        arr[pivotindex] = arr[start]^arr[pivotindex];
        arr[start] = arr[start]^arr[pivotindex];
        arr[pivotindex] = arr[start]^arr[pivotindex];

        int i = start, j = end;
        while (i < j){
            while (i<j && CMP[pivot][arr[j]]<=0)
                j--;
            arr[i] = arr[j];
            while (i<j && CMP[pivot][arr[i]]>=0)
                i++;
            arr[j] = arr[i];
        }
        arr[i] = pivot;
        if (i-1>start) arr=Kwik(CMP,start,i-1, arr);
        if (j+1<end) arr=Kwik(CMP,j+1,end, arr);
        return (arr);
    }


}
