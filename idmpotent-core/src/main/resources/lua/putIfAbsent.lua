-- ARGV[1]=idpKey
local KEY_PREFIX = 'idp-'
local newKJson = ARGV[1]
local expire_seconds = ARGV[2]
redis.log(redis.LOG_DEBUG,tostring(newKJson))
redis.log(redis.LOG_DEBUG,tostring(expire_seconds))
local res = {}
local newK = cjson.decode(newKJson)
local oldK = redis.call("get", KEY_PREFIX .. newK.id)
-- 原key是否存在
if (false == oldK) then
    redis.call("set", KEY_PREFIX .. newK.id, newKJson, "EX", expire_seconds)
    res['idpKey'] = newK
    res['count'] = 1
else
    local oldKJson = cjson.decode(oldK)
    redis.log(redis.LOG_DEBUG,tostring(oldKJson))
    res['idpKey'] = oldKJson
    res['count'] = 0
end
return cjson.encode(res)