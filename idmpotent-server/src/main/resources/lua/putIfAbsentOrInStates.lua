-- ARGV[1] = idpKey, ARGV[2] = states
-- 处理参数
local KEY_PREFIX = 'idp-'
local newKJson = ARGV[1]
local statesJson = ARGV[2]
local expire_seconds = ARGV[3]

redis.log(redis.LOG_DEBUG,tostring(newKJson))
redis.log(redis.LOG_DEBUG,tostring(statesJson))
redis.log(redis.LOG_DEBUG,tostring(expire_seconds))


local newK = cjson.decode(newKJson)
local states = cjson.decode(statesJson)

local res = {}
-- 不存在的情况
local oldKJson = redis.call("get", KEY_PREFIX .. newK.id)
if (false == oldKJson) then -- 不存在时返回值就是false then
    redis.call("set", KEY_PREFIX .. newK.id, newKJson, "EX", expire_seconds)
    res['idpKey'] = newK
    res['count'] = 1
    return cjson.encode(res)
end

-- 已经存在的情况
local existsState = false
for i, state in ipairs(states) do
    if (newK.state == state) then
        existsState = true
    end
end
local oldK = cjson.decode(oldKJson)
if (existsState) then
    -- 存在且  状态包含在states中 then
    redis.call("set", KEY_PREFIX .. newK.id, newKJson, "EX", expire_seconds)
    res['idpKey'] = oldK
    res['count'] = 1
else
    -- 存在且  状态不包含在目标集合内
    res['idpKey'] = oldK
    res['count'] = 0
end
return cjson.encode(res)