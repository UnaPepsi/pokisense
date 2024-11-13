from fastapi import FastAPI, HTTPException, WebSocket, Request
import asyncio
from fastapi.responses import RedirectResponse
import uvicorn
import aiohttp
import random
from string import ascii_letters
from time import time
from hashlib import sha256
from dotenv import load_dotenv, get_key
load_dotenv()

app = FastAPI()

players_using_poki = []
cache = {}

VERSION = get_key('.env','VERSION')
RELEASES = get_key('.env','RELEASES')

def gen_token(uuid: str) -> str:
	return sha256(f'{int(time())}.{uuid}.{"".join(random.choice(ascii_letters) for _ in range(10))}'.encode('utf-8')).hexdigest()

async def validate_uuid(uuid: str) -> str:
	if cache.get(uuid):
		return cache[uuid]
	async with aiohttp.ClientSession() as sess:
		async with sess.get('https://sessionserver.mojang.com/session/minecraft/profile/'+uuid.replace('-','')) as resp:
			if resp.ok:
				name = (await resp.json())['name']
				cache[uuid] = name
				return name
			return ''

#this can obviously be abused but it's supposed to be private and not shown to everyone so it should be fine... right?
@app.websocket('/')
async def on_connect(websocket: WebSocket):
	global players_using_poki #make it a bit faster
	global cache
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
			token = gen_token(uuid_received)
			players_using_poki.append({'uuid':uuid_received,'websocket':websocket,'name':cache[uuid_received],'token':token})
		await websocket.send_json(
			{'type':'auth',
				'data':{
					'token':token
				}
			}
		)
		await websocket.send_json(
					{'type':'connected_players',
						'data':{
							'players':[uuid['uuid'] for uuid in players_using_poki]
							}
						}
					)
		while True:
			await asyncio.wait_for(websocket.receive_text(),10) #heartbeat
			await websocket.send_json(
				{'type':'connected_players',
						'data':{
							'players':[uuid['uuid'] for uuid in players_using_poki]
						}
					}
				)
			await asyncio.sleep(5)
	except: ...
	finally:
		if uuid_received:
			players_using_poki.remove({'uuid':uuid_received,'websocket':websocket,'name':cache[uuid_received],'token':token})
		print('Connection lost',players_using_poki)

@app.post('/broadcast')
async def broadcast(message: str, token: str):
	player_filtered = list(filter(lambda player : player['token']==token,players_using_poki))
	if not player_filtered:
		raise HTTPException(status_code=401,detail='ño')
	for player in players_using_poki:
		await player['websocket'].send_json(
			{'type':'broadcast_message',
				'data':{
					'from':player_filtered[0]['name'],
					'message':message
				}
			}
		)
	return {'status':'ok'}

@app.get('/latestversion')
async def latest_version():
	global VERSION
	global RELEASES
	return {'status':'ok','version':VERSION,'releases':RELEASES}

#received multiple bot/scanner requests so i decided to return them a rick roll response
@app.api_route('/{path:path}',methods=['GET','POST','PUT','PATCH','DELETE'])
async def rick_roll_these_fuckers(request: Request, path: str):
	return RedirectResponse(url='https://youtu.be/dQw4w9WgXcQ?si=z6urq4jDkP0yzyOs')

if __name__ == '__main__':
	uvicorn.run(app,host='0.0.0.0',port=6666)