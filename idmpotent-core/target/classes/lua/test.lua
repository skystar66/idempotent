local newKJson = ARGV[1]
redis.log(redis.LOG_DEBUG, tostring(newKJson))
local newK = cjson.decode(newKJson)
redis.log(redis.LOG_DEBUG, tostring(11222222))
redis.log(redis.LOG_DEBUG, tostring(newK))
redis.log(redis.LOG_DEBUG, tostring(newK.id))
redis.call('set', newK.id, newKJson)
local res = redis.call('get', newK.id)
redis.log(redis.LOG_DEBUG, tostring(res))
return res