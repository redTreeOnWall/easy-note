

export interface RequestData{
  actionId: string;
  sessionId: string;
  body: any;
};

export interface ResponseData<T>{
  isSuccess: boolean;
  erroCode: 'needLogin' | 'err'
  body: T;
};

const url = 'http://localhost:6060/note';

const request = async <T>(data?: RequestData) => {
	const response = await fetch(url, {
		method: 'POST', 
		cache: 'no-cache', 
		headers: {
			'Content-Type': 'application/text'
		},
		body: JSON.stringify(data)
	});
	const responseData = await (response.json() as Promise<ResponseData<T>>);
} 

export default  request;
