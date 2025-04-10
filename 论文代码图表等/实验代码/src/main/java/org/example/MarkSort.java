package org.example;

import java.util.*;

public class MarkSort {

    /**
     * my算法一 ： 本地数据的收集，包括了添加噪声
     * 输惨的参数包括了用户排序数据，查询数K 和隐私预算
     * @return int[] : {Xjk, j, k} 下标j、k以及打分的插值
     */
    public ArrayList<int[]> markSortpart1(List<ArrayList<Integer>>data, int K, double epsilon, double u){  //u在这其实就是m-1
        int m = data.get(0).size();
//        System.out.println(m);
        double epsilon1 = epsilon/K;
        ArrayList<int[]>res=  new ArrayList<>();
        for(ArrayList<Integer> linedata: data){   //对n个用户的询问
            HashMap<Integer, Integer> map = new HashMap<>();
            for(int i =0; i<linedata.size(); i++)
                map.put(linedata.get(i)-1, i); //!!!!!!!!!!!!!!!!!!
            for(int i=0; i<K; i++){
                Random random = new Random();
                int j = random.nextInt(m);  //生成0——m-1之间的整数
                int k = random.nextInt(m);
                while(k == j)   //确保jk不相同
                    k = random.nextInt(m);
                double xjk = map.get(k)-map.get(j)+u;    //算出分数的差值！！！！
                double r = Math.random();
                double bound = (double) 1/(Math.pow( Math.E,epsilon1)+1 ) +
                        (xjk/(2*u))*( (Math.pow(Math.E,epsilon1)-1)/(Math.pow(Math.E, epsilon1)+1) );
                if(r <= bound )
                    res.add(new int[]{1, j, k});
                else
                    res.add(new int[]{0, j, k});
            }
        }
        return res;
    }


    /**
     * my算法二：  数据的处理
     * @return
     */
    public ArrayList<double[]> markSortpart2(List<ArrayList<Integer>>data, ArrayList<int[]> R, double epsilon1){
        int m = data.get(0).size();
        double[][]S = new double[m][m];
        int[][]C = new int[m][m];
        for(int[] xjk: R){
            int j = xjk[1], k = xjk[2];
            S[j][k] += xjk[0];
            C[j][k] += 1;
            //这里在算法里补充！！！！！！！！！！！！！！！！！！
            S[k][j] += ((xjk[0]+1)%2);
            C[k][j] ++;
        }
        for(int i=0; i<m; i++){
            for(int j=0; j<m; j++){
                if(i == j)continue;    //这个！！！！！！！！！！！！！！！！坑了我好久！！！！！！！！！！！！！！！！！！
                if(C[i][j] == 0)C[i][j] = 1;
                S[i][j] = S[i][j]* ( (Math.pow(Math.E,epsilon1)+1)/(Math.pow(Math.E, epsilon1)-1) )
                        *((double) 2*(m-1)/C[i][j] ) - 2*(m-1)/( Math.pow(Math.E, epsilon1)-1 );
            }
        }
        double L[] = new double[m];
        for(int i=0; i<m; i++){
            for(int j=0; j<m; j++){
                L[i] += S[i][j];
            }
        }
        ArrayList<double[]>res =  new ArrayList<double[]>();
        for(int i=0; i<m; i++){
            res.add(new double[]{i+1 , L[i]/(m-1)-(m-1)} );   //[-u, u] :  表示与中体均值的偏移量
        }
        Collections.sort(res,((o1, o2) -> o2[1]-o1[1]>0? 1:-1));
        return res;
    }


}
