// MCP to OpenAI Function Calling Proxy
const express = require('express');
const axios = require('axios');

const app = express();
app.use(express.json());

const MCP_BASE_URL = 'http://localhost:8081/mcp';

// Convert MCP tools to OpenAI function format
app.get('/functions', async (req, res) => {
  try {
    const response = await axios.get(`${MCP_BASE_URL}/tools`);
    const mcpTools = response.data.tools;
    
    const openAIFunctions = mcpTools.map(tool => ({
      name: tool.name,
      description: tool.description,
      parameters: tool.input_schema
    }));
    
    res.json({ functions: openAIFunctions });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Execute MCP tool calls
app.post('/execute', async (req, res) => {
  try {
    const { name, arguments: args } = req.body;
    
    const response = await axios.post(`${MCP_BASE_URL}/call`, {
      name,
      arguments: args
    });
    
    res.json(response.data);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.listen(3000, () => {
  console.log('MCP Proxy running on http://localhost:3000');
});