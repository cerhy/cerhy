package com.jeecms.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.spy.memcached.compat.CloseUtil;

import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.jeecms.cms.entity.main.Content;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class RedisUtil {
	private static final Logger logger = Logger
			.getLogger(RedisUtil.class);
	private static JedisPool jedisPool = null;

	/**
	 * 初始化Redis连接池
	 */
	static {
		try {
			WebApplicationContext wac = ContextLoader
					.getCurrentWebApplicationContext();
			jedisPool = (JedisPool) wac.getBean("jedisPool");

		} catch (Exception e) {
			logger.error("redis初始化异常...");
		}
	}

	/**
	 * 获取Jedis实例
	 * 
	 * @return
	 */
	public synchronized static Jedis getJedis() {
		Jedis resource = null;
		//int count=0;
	
			try {
				if (jedisPool != null) {
					resource = jedisPool.getResource();
					return resource;
				} else {
					return null;
				}
			} catch (Exception e) {
				logger.error("redis连接异常，切换回数据库读取",e);
				return null;
			}
	      
	}

	/**
	 * 释放jedis资源
	 * 
	 * @param jedis
	 */
	public static void returnResource(final Jedis jedis) {
		if (jedis != null) {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 
	 * 
	 * @param jedis
	 */
	public static List<Content> getList(String key) {
		List<Content> list = null;
		Jedis jedis = RedisUtil.getJedis();
		try {
			if(jedis!=null){
				 byte[] in = jedis.get(key.getBytes());  
			        list = (List<Content>)ListTranscoder.deserialize(in);  
			}else{
				return list;
			}
		} catch (Exception e) {
			logger.error("getList()异常...",e);
			returnResource(jedis);
		} finally {
			returnResource(jedis);
		}

		return list;
	}
	public static void setList(String key,List<Content> list) {
		Jedis jedis = RedisUtil.getJedis();
		try {
			if(jedis!=null){
				jedis.set(key.getBytes(), ListTranscoder.serialize(list));  
			}
		} catch (Exception e) {
			logger.error("setList()异常",e); 
			returnResource(jedis);
		} finally {
			returnResource(jedis);
		}
		
	}
	
	
	
	/** 
     * 移除列表元素，返回移除的元素数量 
     * @param key 
     * @param count，标识，表示动作或者查找方向 
     * <li>当count=0时，移除所有匹配的元素；</li> 
     * <li>当count为负数时，移除方向是从尾到头；</li> 
     * <li>当count为正数时，移除方向是从头到尾；</li> 
     * @param value 匹配的元素 
     * @return Long 
     */  
    public static Long lrem(String key, long count, String cid,List<Content> list){  
    	Jedis jedis = RedisUtil.getJedis();
    	Long length=null;
		try {
			if(jedis!=null){
				if(getList(key)!=null&&getList(key).size()>0){
					for(int i=0;i<getList(key).size();i++){
						if(!cid.equals(getList(key).get(i).getId().toString())){
							list.add(getList(key).get(i));
						}
					}
					setList(key, list);
				}
			}
		} catch (Exception e) {
			logger.error("redis在移除时候抛出异常",e); 
			returnResource(jedis);
		} finally {
			returnResource(jedis);
		}
        return length;  
    }
	
	
	public static void close(Closeable closeable) {  
        if (closeable != null) {  
            try {  
                closeable.close();  
            } catch (Exception e) {  
                logger.error("Unable to close %s",e);  
            }  
        }  
    }  
  
    static class User implements Serializable{  
        String name;  
  
        public String getName() {  
            return name;  
        }  
  
        public void setName(String name) {  
            this.name = name;  
        }  
    }  
  
    static class ObjectsTranscoder{  
          
        public static byte[] serialize(List<User> value) {  
            if (value == null) {  
                throw new NullPointerException("Can't serialize null");  
            }  
            byte[] rv=null;  
            ByteArrayOutputStream bos = null;  
            ObjectOutputStream os = null;  
            try {  
                bos = new ByteArrayOutputStream();  
                os = new ObjectOutputStream(bos);  
                for(User user : value){  
                    os.writeObject(user);  
                }  
                os.writeObject(null);  
                os.close();  
                bos.close();  
                rv = bos.toByteArray();  
            } catch (IOException e) {  
                throw new IllegalArgumentException("Non-serializable object", e);  
            } finally {  
                close(os);  
                close(bos);  
            }  
            return rv;  
        }  
  
        public static List<User> deserialize(byte[] in) {  
            List<User> list = new ArrayList<User>();  
            ByteArrayInputStream bis = null;  
            ObjectInputStream is = null;  
            try {  
                if(in != null) {  
                    bis=new ByteArrayInputStream(in);  
                    is=new ObjectInputStream(bis);  
                    while (true) {  
                        User user = (User) is.readObject();  
                        if(user == null){  
                            break;  
                        }else{  
                            list.add(user);  
                        }  
                    }  
                    is.close();  
                    bis.close();  
                }  
            } catch (IOException e) {  
                logger.error("Caught IOException decoding %d bytes of data",e);  
            } catch (ClassNotFoundException e) {  
                logger.error("Caught CNFE decoding %d bytes of data",e);  
            } finally {  
                CloseUtil.close(is);  
                CloseUtil.close(bis);  
            }  
            return list;  
        }  
    }  
      
    static class ListTranscoder{  
        public static byte[] serialize(Object value) {  
            if (value == null) {  
                throw new NullPointerException("Can't serialize null");  
            }  
            byte[] rv=null;  
            ByteArrayOutputStream bos = null;  
            ObjectOutputStream os = null;  
            try {  
                bos = new ByteArrayOutputStream();  
                os = new ObjectOutputStream(bos);  
                os.writeObject(value);  
                os.close();  
                bos.close();  
                rv = bos.toByteArray();  
            } catch (IOException e) {  
                throw new IllegalArgumentException("Non-serializable object", e);  
            } finally {  
                close(os);  
                close(bos);  
            }  
            return rv;  
        }  
  
        public static Object deserialize(byte[] in) {  
            Object rv=null;  
            ByteArrayInputStream bis = null;  
            ObjectInputStream is = null;  
            try {  
                if(in != null) {  
                    bis=new ByteArrayInputStream(in);  
                    is=new ObjectInputStream(bis);  
                    rv=is.readObject();  
                    is.close();  
                    bis.close();  
                }  
            } catch (IOException e) {  
                logger.error("Caught IOException decoding %d bytes of data", e);
            } catch (ClassNotFoundException e) {  
                logger.error("Caught CNFE decoding %d bytes of data",e);  
            } finally {  
                CloseUtil.close(is);  
                CloseUtil.close(bis);  
            }  
            return rv;  
        }  
    }  
}  
	
