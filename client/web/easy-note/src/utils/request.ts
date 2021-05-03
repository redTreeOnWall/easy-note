
export interface RequestData{};

const request = async (url: string, data?: RequestData  ) => {
	const response = await fetch(url, {
		method: 'POST', 
		cache: 'no-cache', 
		headers: {
			'Content-Type': 'application/text'
		},
		body: JSON.stringify(data)
	});
	return response.json();
} 

export default  request;
