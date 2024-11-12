from fastapi import FastAPI, WebSocket, Request
import asyncio
from fastapi.responses import RedirectResponse
import uvicorn
import aiohttp

app = FastAPI()

players_using_poki = []

cache = {}

async def validate_uuid(uuid: str) -> bool:
    if cache.get(uuid):
        return cache[uuid]
    async with aiohttp.ClientSession() as sess:
        async with sess.get('https://sessionserver.mojang.com/session/minecraft/profile/'+uuid.replace('-','')) as resp:
            cache[uuid] = resp.ok
            return resp.ok

#this can obviously be abused but it's supposed to be private and not shown to everyone so it should be fine... right?
@app.websocket('/')
async def on_connect(websocket: WebSocket):
    global players_using_poki #make it a bit faster
    print('Connection received')
    await websocket.accept()
    uuid_received = ''
    try:
        uuid_received = await websocket.receive_text()
        if not await validate_uuid(uuid_received):
            print('Invalid UUID')
            await websocket.close()
            return
        if not uuid_received in players_using_poki:
            players_using_poki.append(uuid_received)
        while True:
            await websocket.send_json({'players':players_using_poki})
            await asyncio.sleep(5)
    except: ...
    finally:
        if uuid_received:
            players_using_poki.remove(uuid_received)
        print('Connection lost',players_using_poki)

#received multiple bot/scanner requests so i decided to return them a rick roll response
@app.api_route('/{path:path}',methods=['GET','POST','PUT','PATCH','DELETE'])
async def rick_roll_these_fuckers(request: Request, path: str):
    return RedirectResponse(url='https://youtu.be/dQw4w9WgXcQ?si=z6urq4jDkP0yzyOs')

if __name__ == '__main__':
    uvicorn.run(app,host='0.0.0.0',port=6666)