package com.xuef.service;

import org.apache.commons.lang.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用后缀树实现敏感词过滤
 * Created by moveb on 2018/10/3.
 */
@Service
public class SensitiveFilterService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilterService.class);
    private static final String DEFAULT_REPLACEMENT = "****";
    TrieNode root = new TrieNode();

    @Override
    public void afterPropertiesSet() throws Exception {
        root = new TrieNode();

        try {
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                lineTxt = lineTxt.trim();
                insert(lineTxt);
            }
            read.close();
        } catch (Exception e) {
            logger.error("read SensitiveWords.txt failed: " + e.getMessage());
        }
    }

    private class TrieNode{
        // 是否是叶子节点，即结束字符
        boolean isEnd = false;
        // 后序节点
        Map<Character, TrieNode> suffixes = new HashMap<>();

        void addSubNode(Character key, TrieNode node) {
            suffixes.put(key, node);
        }

        TrieNode getSubNode(Character key) {
            return suffixes.get(key);
        }
        public int getSubNodeNum() {
            return suffixes.size();
        }
    }
    // 以一行文本为单位，将每个字符一个个插入前缀树
    void insert(String oneLineTxt){
        TrieNode tRoot = root;
        for(int i=0; i<oneLineTxt.length(); i++){
            Character ch = oneLineTxt.charAt(i);
            TrieNode suffixNode = tRoot.getSubNode(ch);
            // 如果ch不是tRoot的后继
            if(suffixNode == null){
                // 因为null表示没有这个节点，所以这里要初始化该节点以表示插入ch为
                suffixNode = new TrieNode();
                tRoot.addSubNode(ch, suffixNode);
            }
            // ch 是后继
            tRoot = suffixNode;
            // 如果ch是行尾字符，那么将其标记为结束字符
            if(i+1 == oneLineTxt.length()){
                suffixNode.isEnd = true;
            }
        }
    }

    /**
     * 是否是特殊字符(即不是Ascii字符 && 不是东亚文字字符)
     * @param c
     * @return
     */
    boolean isSpecialSymbol(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF范围内 是东亚文字
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }
    public String filterSensitive(String rawTxt){
        StringBuilder filteredTxt = new StringBuilder();
        int pointer1 = 0;
        TrieNode pointer3 = root;
        while(pointer1 < rawTxt.length()) {
            for (int i = pointer1; i < rawTxt.length(); i++) {
                Character ch = rawTxt.charAt(i);
                // 忽略特殊字符
                if(isSpecialSymbol(ch)){
                    continue;
                }
                TrieNode t = pointer3.getSubNode(ch);
                if(t == null){
                    // 说明不存在以ch开头的敏感词
                    filteredTxt.append(rawTxt.charAt(pointer1));
                    pointer1++;
                    pointer3 = root;
                    break;
                }else if(t.isEnd){
                    // 发现敏感词，pointer1--i(包括i)
                    filteredTxt.append(DEFAULT_REPLACEMENT);
                    pointer1 = i+1;
                    pointer3 = root;
                    break;
                }else{
                    pointer3 = pointer3.getSubNode(ch);
                }
            }
        }
        return filteredTxt.toString();
    }


    public static void main(String[] argv) {
        SensitiveFilterService s = new SensitiveFilterService();
        s.insert("色情");
        s.insert("好色");
        System.out.print(s.filterSensitive("一起来色情"));
    }
}
